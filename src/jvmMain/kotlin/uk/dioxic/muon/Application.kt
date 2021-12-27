@file:Suppress("unused")

package uk.dioxic.muon

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.json.Json
import org.koin.core.logger.Level.ERROR
import org.koin.dsl.module
import org.koin.ktor.ext.Koin
import org.koin.logger.slf4jLogger
import uk.dioxic.muon.exceptions.IdNotFoundException
import uk.dioxic.muon.exceptions.MusicImportException
import uk.dioxic.muon.repository.*
import uk.dioxic.muon.service.MusicService
import uk.dioxic.muon.service.MusicServiceImpl
import kotlin.io.path.ExperimentalPathApi

val muonHome = "${System.getenv("HOMEPATH")}/.muon"

private val appModule = module {
    single<ConfigRepository> { ConfigRepositoryImpl(configDirectory = muonHome) }
    single<LibraryRepository> { LibraryRepositoryImpl(get()) }
    single<MusicRepository> { MusicRepositoryImpl("index") }
    single<MusicService> { MusicServiceImpl(get()) }
    single { ShoppingRepository() }
}

fun main(args: Array<String>) {
    System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager")
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}

@ExperimentalPathApi
fun Application.main() {
    val env = environment.config.property("ktor.environment").getString()
    install(CallLogging)
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = env == "dev"
        })
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
    install(Koin) {
        slf4jLogger(level = ERROR)
        modules(appModule)
    }
    install(StatusPages) {
        exception<IdNotFoundException> { cause ->
            call.respond(HttpStatusCode.NotFound, "file Id [${cause.id}] not found")
        }
        exception<MusicImportException> { cause ->
            call.respond(HttpStatusCode.NotModified, cause.errors)
        }
        exception<IllegalStateException> { cause ->
            call.respond(HttpStatusCode.BadRequest, cause.message ?: "")
        }
    }

    routing {
        shoppingList()
        library()
        music()
        config()
        index()

        static("/") {
            resources("")
        }

        get("/env") {
            call.respondText(env)
        }
    }
}