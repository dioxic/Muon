@file:Suppress("unused")

package uk.dioxic.muon

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.coroutines.FlowPreview
import kotlinx.serialization.json.Json
import org.koin.core.logger.Level
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.dsl.onClose
import org.koin.logger.slf4jLogger
import uk.dioxic.muon.common.Global
import uk.dioxic.muon.exceptions.IdNotFoundException
import uk.dioxic.muon.exceptions.MusicImportException
import uk.dioxic.muon.repository.LuceneRepository
import uk.dioxic.muon.repository.RekordboxRepository
import uk.dioxic.muon.repository.SettingsRepository
import uk.dioxic.muon.server.*
import uk.dioxic.muon.service.ImportService
import uk.dioxic.muon.service.SearchService
import kotlin.io.path.ExperimentalPathApi
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime

private val appModule = module {
    single { LuceneRepository(Global.homePath.resolve("index")) } onClose {
        it?.close()
    }
    singleOf(::RekordboxRepository) onClose {
        it?.close()
    }
    singleOf(::SearchService)
    single { SettingsRepository(Global.homePath) }
    singleOf(::ImportService)
}

fun main(args: Array<String>) {
    System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager")
    embeddedServer(Netty, commandLineEnvironment(args)).start(wait = true)
}

@FlowPreview
@ExperimentalTime
@ExperimentalPathApi
fun Application.main() {
    val env = environment.config.property("ktor.environment").getString()
    val isDevelopment = env == "dev"

    install(CallLogging)
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = isDevelopment
        })
    }
    install(CORS) {
//        host("0.0.0.0:5000")
        anyHost()
        header(HttpHeaders.ContentType)
    }
    install(Compression) {
        gzip()
    }
    install(CachingHeaders) {
        options { outgoingContent ->
            if (!isDevelopment) {
                when (outgoingContent.contentType?.withoutParameters()) {
                    ContentType.Image.XIcon, ContentType.Image.PNG, ContentType.Image.JPEG ->
                        CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 7.days.inWholeSeconds.toInt()))
                    ContentType.Application.JavaScript, ContentType.Text.CSS ->
                        CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 7.days.inWholeSeconds.toInt()))
                    else -> null
                }
            } else {
                null
            }
        }
    }
    install(Koin) {
        slf4jLogger(level = Level.ERROR)
        modules(appModule)
    }
    install(Sessions) {
        apiSessionCookie(isDevelopment)
    }
    install(Csrf) {
        validateHeader("X-CSRF") { it.call.getCsrfToken() }
    }
    install(StatusPages) {
        exception<IdNotFoundException> { call, cause ->
            call.respondText("file Id [${cause.id}] not found", status = HttpStatusCode.NotFound)
        }
        exception<MusicImportException> { call, cause ->
            call.respond(HttpStatusCode.NotModified, cause.errors)
        }
        exception<IllegalStateException> { call, cause ->
            call.respondText(cause.message ?: "", status = HttpStatusCode.BadRequest)
        }
    }

    routing {
        settings()
        tracks()
        lucene()
        import()
        indexHtml()

        static("/") {
            resources("static")
        }

        get("/env") {
            call.respondText(env)
        }
    }
}

