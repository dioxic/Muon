package uk.dioxic.muon

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.FlowPreview
import org.koin.ktor.ext.inject
import uk.dioxic.muon.common.Global
import uk.dioxic.muon.config.Config
import uk.dioxic.muon.config.Settings
import uk.dioxic.muon.repository.LibraryRepository
import uk.dioxic.muon.repository.ShoppingRepository
import uk.dioxic.muon.service.MusicService
import kotlin.io.path.ExperimentalPathApi
import kotlin.time.ExperimentalTime

fun Routing.shoppingList() {

    val shoppingRepo by inject<ShoppingRepository>()

    route(shoppingListPath) {
        get {
            call.respond(shoppingRepo.get())
        }
        post {
            shoppingRepo.add(call.receive())
            call.respond(HttpStatusCode.OK)
        }
        delete("/{id}") {
            val id = call.parameters["id"]?.toInt() ?: error("Invalid delete request")
            shoppingRepo.delete(id)
            call.respond(HttpStatusCode.OK)
        }
    }
}

//@FlowPreview
//@ExperimentalTime
//fun Routing.import() {
//    val musicService by inject<MusicService>()
//    val libraryRepository by inject<LibraryRepository>()
//
//    route(importPath) {
//        get {
//            val libraryId = call.parameters["library"] ?: error("Invalid request - library not specified")
//            val maxResults = call.parameters["maxResults"]?.toIntOrNull() ?: 500
//            if (call.parameters["refresh"].toBoolean()) {
//                musicService.refreshIndex(libraryRepository.getLibraryById(libraryId))
//            }
//
//            call.respond(
//                musicService.getAudioDetails(
//                    libraryId = libraryId,
//                    maxResults = maxResults
//                )
//            )
//        }
//    }
//}

@FlowPreview
@ExperimentalTime
fun Routing.music() {
    val musicService by inject<MusicService>()
    val libraryRepository by inject<LibraryRepository>()

    route(musicPath) {
        get {
            val libraryId = call.parameters["library"]
            val maxResults = call.parameters["maxResults"]?.toIntOrNull() ?: 500
            val includeDuplicates = call.parameters["includeDuplicates"].toBoolean()
            val query = call.parameters["q"]
            val sort = call.parameters["sort"]
            val sortReverse = call.parameters["sortReverse"].toBoolean()
            val after = call.parameters["after"]?.toIntOrNull()

            if (call.parameters["refresh"]?.toBoolean() == true) {
                if (libraryId == null) {
                    error("Invalid delete request - refresh parameter not valid without a library specified")
                }
                musicService.refreshIndex(libraryRepository.getLibraryById(libraryId))
            }

            val details = musicService.search(
                libraryId = libraryId,
                text = query,
                maxResults = maxResults,
                sortField = sort,
                sortReverse = sortReverse,
                after = after
            )

            if (includeDuplicates) {
                call.respond(musicService.attachDuplicates(details))
            } else {
                call.respond(details)
            }
        }
        get("/{id}") {
            call.respond(musicService.getById(call.parameters["id"]!!))
        }
        patch {
            call.respond(
                musicService.updateMany(
                    libraryId = call.parameters["library"], audioFiles = call.receive()
                )
            )
        }
        delete("/{id}") {
            musicService.deleteById(call.parameters["id"]!!)
            call.respond(HttpStatusCode.OK)
        }
    }
}

@ExperimentalPathApi
fun Routing.settings() {

    route(settingsPath) {
//        subconfig(
//            key = AudioImportConfig.path,
//            getFn = { configRepository.getImportConfig() },
//            setFn = { configRepository.save(it) }
//        )
//        subconfig(
//            key = LibraryConfig.path,
//            getFn = { configRepository.getLibraryConfig() },
//            setFn = { configRepository.save(it) }
//        )

        get {
            call.respond(Global.settings)
        }
        post {
            Global.settings = call.receive() as Settings
            call.respond(HttpStatusCode.OK)
        }
    }
}

@ExperimentalPathApi
inline fun <reified T : Config> Route.subconfig(
    key: String,
    noinline getFn: () -> T,
    crossinline setFn: (T) -> Unit
) {
    route("/$key") {
        get {
            call.respond(getFn.invoke())
        }
        post {
            setFn(call.receive())
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Routing.index() {
    get("/") {
        call.respondText(
            this::class.java.classLoader.getResource("static/index.html")!!.readText(),
            ContentType.Text.Html
        )
    }
}