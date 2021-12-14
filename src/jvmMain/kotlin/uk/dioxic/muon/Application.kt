@file:Suppress("unused")

package uk.dioxic.muon

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jaudiotagger.audio.AudioFileIO
import org.koin.dsl.module
import org.koin.ktor.ext.Koin
import org.koin.logger.slf4jLogger
import uk.dioxic.muon.repository.*
import uk.dioxic.muon.service.LibraryService
import uk.dioxic.muon.service.LibraryServiceImpl
import java.util.logging.Level
import kotlin.io.path.ExperimentalPathApi

val appModule = module {
    single<ConfigRepository> { ConfigRepositoryImpl(configDirectory = "${System.getenv("HOMEPATH")}/.muon") }
    single<LibraryRepository> { LibraryRepositoryImpl() }
    single<LibraryService> { LibraryServiceImpl(get(), get()) }
    single { ShoppingRepository() }
}

fun main(args: Array<String>) {
    System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager")
    AudioFileIO.logger.level = Level.OFF
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}

@ExperimentalPathApi
fun Application.main() {
    install(CallLogging)
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
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }

    routing {
        shoppingList()
        library()
        config()
        index()

        static("/") {
            resources("")
        }
    }
}