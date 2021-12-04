package uk.dioxic.muon

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import uk.dioxic.muon.audio.AudioFile
import uk.dioxic.muon.config.AudioImportConfig
import uk.dioxic.muon.config.Config
import uk.dioxic.muon.config.LibraryConfig
import uk.dioxic.muon.repository.ConfigRepository
import uk.dioxic.muon.repository.LibraryRepository
import uk.dioxic.muon.repository.ShoppingRepository
import uk.dioxic.muon.service.ImportService
import java.io.File
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

fun Routing.import() {
    val importService by inject<ImportService>()

    route(importPath) {
        get {
            if (call.parameters["reload"]?.toBoolean() == true) {
                importService.reload()
            }
            call.respond(importService.getImportFiles())
        }
        post {
            importService.save(call.receive())
            call.respond(HttpStatusCode.OK)
        }
        delete("/{id}") {
            val id = call.parameters["id"] ?: error("Invalid delete request")
            importService.delete(id)
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

    route(ConfigMap.path) {
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