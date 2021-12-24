package uk.dioxic.muon

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import uk.dioxic.muon.config.AudioImportConfig
import uk.dioxic.muon.config.Config
import uk.dioxic.muon.config.LibraryConfig
import uk.dioxic.muon.repository.ConfigRepository
import uk.dioxic.muon.repository.LibraryRepository
import uk.dioxic.muon.repository.MusicRepository
import uk.dioxic.muon.repository.ShoppingRepository
import uk.dioxic.muon.service.MusicService
import kotlin.io.path.ExperimentalPathApi

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

fun Routing.music() {
    val musicService by inject<MusicService>()
    val libraryRepository by inject<LibraryRepository>()

    route(musicPath) {
        get {
            val libraryId = call.parameters["library"]
            val maxResults = call.parameters["maxResults"]?.toIntOrNull() ?: 500
            val query = call.parameters["q"]

            if (call.parameters["refresh"]?.toBoolean() == true) {
                if (libraryId == null) {
                    error("Invalid delete request - refresh parameter not valid without a library specified")
                }
                musicService.refreshIndex(libraryRepository.getLibraryById(libraryId))
            }
            call.respond(
                musicService.search(
                    libraryId = libraryId,
                    text = query,
                    maxResults = maxResults
                )
            )
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

fun Routing.library() {
    val libraryRepository by inject<LibraryRepository>()

    route(libraryPath) {
        get {
            call.respond(libraryRepository.getLibraries())
        }
        get("/{id}") {
            call.respond(libraryRepository.getLibraryById(call.parameters["id"]!!))
        }
        post {
            libraryRepository.saveLibrary(call.receive())
            call.respond(HttpStatusCode.OK)
        }
        delete("/{id}") {
            libraryRepository.deleteLibrary(call.parameters["id"]!!)
            call.respond(HttpStatusCode.OK)
        }
    }
}

//fun Routing.audioFile() {
//    val configRepository by inject<ConfigRepository>()
//
//    route(AudioFile.path) {
//        get {
//            call.respond(readAudioFiles(File(configRepository.getLibraryConfig().libraryPath)))
//        }
//    }
//}

@ExperimentalPathApi
fun Routing.config() {
    val configRepository by inject<ConfigRepository>()

    route(configPath) {
        subconfig(
            key = AudioImportConfig.path,
            getFn = { configRepository.getImportConfig() },
            setFn = { configRepository.save(it) }
        )
        subconfig(
            key = LibraryConfig.path,
            getFn = { configRepository.getLibraryConfig() },
            setFn = { configRepository.save(it) }
        )

        get {
            call.respond(configRepository.getFullConfig())
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
            this::class.java.classLoader.getResource("index.html")!!.readText(),
            ContentType.Text.Html
        )
    }
}