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
import uk.dioxic.muon.repository.ConfigRepository
import uk.dioxic.muon.repository.ConfigRepositoryImpl
import uk.dioxic.muon.repository.ShoppingRepository
import java.util.logging.Level
import kotlin.io.path.ExperimentalPathApi

val appModule = module {
    single<ConfigRepository> { ConfigRepositoryImpl(configDirectory = "${System.getenv("HOMEPATH")}/.muon") }
    single { ShoppingRepository() }
}

fun main(args: Array<String>) {
    AudioFileIO.logger.level = Level.OFF
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}

@ExperimentalPathApi
fun Application.main() {
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
        audioFile()
        import()
        config()
        index()

        static("/") {
            resources("")
        }
    }
}