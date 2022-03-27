package uk.dioxic.muon

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.toList
import org.koin.ktor.ext.inject
import uk.dioxic.muon.common.Global
import uk.dioxic.muon.config.Config
import uk.dioxic.muon.config.Settings
import uk.dioxic.muon.repository.*
import uk.dioxic.muon.service.MusicService
import uk.dioxic.muon.service.OldMusicService
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

fun Routing.search() {
    val luceneRepository by inject<LuceneRepository>()
    val rekordboxRepository by inject<RekordboxRepository>()

    route("/search") {
        get {
            val maxResults = call.parameters["maxResults"]?.toIntOrNull() ?: 500
            val query = call.parameters["q"]
            val trackIds = luceneRepository.search(query, maxResults)
            val tracks = rekordboxRepository.getRekordboxTracksById(trackIds)
            call.respond(tracks.toList(mutableListOf()))
        }
    }
}

fun Routing.lucene() {
    val musicService by inject<MusicService>()
//    val luceneRepository by inject<LuceneRepository>()

    route("/index") {
        get("/rebuild") {
            val count = musicService.buildIndex()
            call.respond("rebuilt index for $count tracks")
        }
        get("/refresh") {
            val count = musicService.refreshIndex()
            call.respond("refreshed index for $count tracks")
        }
//        get("/drop") {
//            luceneRepository.dropIndex()
//            call.respond(HttpStatusCode.OK)
//        }
    }
}

@FlowPreview
@ExperimentalTime
fun Routing.music() {
    val oldMusicService by inject<OldMusicService>()
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
                oldMusicService.refreshIndex(libraryRepository.getLibraryById(libraryId))
            }

            val details = oldMusicService.search(
                libraryId = libraryId,
                text = query,
                maxResults = maxResults,
                sortField = sort,
                sortReverse = sortReverse,
                after = after
            )

            if (includeDuplicates) {
                call.respond(oldMusicService.attachDuplicates(details))
            } else {
                call.respond(details)
            }
        }
        get("/{id}") {
            call.respond(oldMusicService.getById(call.parameters["id"]!!))
        }
        patch {
            call.respond(
                oldMusicService.updateMany(
                    libraryId = call.parameters["library"], audioFiles = call.receive()
                )
            )
        }
        delete("/{id}") {
            oldMusicService.deleteById(call.parameters["id"]!!)
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