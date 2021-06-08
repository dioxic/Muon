package uk.dioxic.muon

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import org.jaudiotagger.audio.AudioFileIO
import java.io.File
import java.util.*
import java.util.logging.Level
import kotlin.io.path.ExperimentalPathApi
import uk.dioxic.muon.config.ConfigDal
import uk.dioxic.muon.ConfigKey.*
import uk.dioxic.muon.config.AudioImportConfig
import uk.dioxic.muon.audio.AudioFile
import uk.dioxic.muon.audio.AudioFileImport

val shoppingList = mutableListOf(
    ShoppingListItem("Cucumbers 🥒", 1),
    ShoppingListItem("Tomatoes 🍅", 2),
    ShoppingListItem("Orange Juice 🍊", 3)
)

@ExperimentalPathApi
val configDal = ConfigDal("${System.getenv("HOMEPATH")}/.muon")

fun main(args: Array<String>) {
    AudioFileIO.logger.level = Level.OFF
    io.ktor.server.netty.EngineMain.main(args)
}

@ExperimentalPathApi
fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    install(CORS) {
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Delete)
        anyHost()
    }
    install(Compression) {
        gzip()
    }
    routing {
//        static("/static") {
//            resources("static")
//        }
        route(ShoppingListItem.path) {
            get {
                call.respond(shoppingList)
            }
            post {
                shoppingList += call.receive<ShoppingListItem>()
                call.respond(HttpStatusCode.OK)
            }
            delete("/{id}") {
                val id = call.parameters["id"]?.toInt() ?: error("Invalid delete request")
                shoppingList.removeIf { it.id == id }
                call.respond(HttpStatusCode.OK)
            }
        }
        route(AudioFile.path) {
            get {
                call.respond(readAudioFiles(File(configDal.library().importPath)))
            }
        }
        route(AudioFileImport.path) {
            get {
                call.respond(readAudioFiles(File(configDal.library().importPath)).map { it.format() })
            }
        }
        route("/config") {
            route("/{key}") {
                get {
                    val key = call.parameters["key"] ?: error("Invalid config get request")
                    val configKey = ConfigKey.valueOf(key)
                    call.respond(configDal.get(configKey))
                }
                post {
                    requireNotNull(call.parameters["key"]) { "Config key required" }
                    when (val configKey = ConfigKey.valueOf(call.parameters["key"]!!)) {
                        AudioImport -> configDal.set(configKey, call.receive<AudioImportConfig>())
                    }
                    call.respond(HttpStatusCode.OK)
                }
            }
            get {
                call.respond(configDal.get())
            }
        }
        get("/") {
            call.respondText(
                this::class.java.classLoader.getResource("index.html")!!.readText(),
                ContentType.Text.Html
            )
        }
        static("/") {
            resources("")
        }
    }
}