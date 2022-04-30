@file:Suppress("unused")

package uk.dioxic.muon

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.partialcontent.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.json.Json
import org.koin.core.logger.Level
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.dsl.onClose
import org.koin.logger.slf4jLogger
import uk.dioxic.muon.common.Global
import uk.dioxic.muon.exceptions.CsrfInvalidException
import uk.dioxic.muon.exceptions.IdNotFoundException
import uk.dioxic.muon.repository.LuceneRepository
import uk.dioxic.muon.repository.RekordboxRepository
import uk.dioxic.muon.repository.SettingsRepository
import uk.dioxic.muon.server.*
import uk.dioxic.muon.server.plugins.CsrfPlugin
import uk.dioxic.muon.server.plugins.KoinPlugin
import uk.dioxic.muon.service.SearchService
import uk.dioxic.muon.service.TrackService
import kotlin.time.Duration.Companion.days

private val appModule = module {
    single { LuceneRepository(Global.configPath.resolve("index")) } onClose {
        it?.close()
    }
    singleOf(::RekordboxRepository) onClose {
        it?.close()
    }
    singleOf(::SearchService)
    single { SettingsRepository(Global.configPath) }
    singleOf(::TrackService)
}

fun main(args: Array<String>) {
    System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager")
    EngineMain.main(args)
}

fun Application.plugins() {
    val env = environment.config.property("ktor.environment").getString()
    val isDevelopment = env == "dev"
    val useCsrf = environment.config.property("ktor.useCsrf").getString().toBoolean()

    install(CallLogging) {
        filter { call ->
            call.request.path().startsWith("/api")
        }
    }
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = isDevelopment
            encodeDefaults = true
        })
    }
    install(PartialContent)
    install(AutoHeadResponse)
//    install(CORS) {
//        host("0.0.0.0:5000")
//        anyHost()
//        header(HttpHeaders.ContentType)
//    }
    install(Compression) {
        gzip()
    }
    install(CachingHeaders) {
        options { _, outgoingContent ->
            if (!isDevelopment) {
                when (outgoingContent.contentType?.withoutParameters()) {
                    ContentType.Image.XIcon, ContentType.Image.PNG, ContentType.Image.JPEG,
                    ContentType.Application.JavaScript, ContentType.Text.CSS ->
                        CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 7.days.inWholeSeconds.toInt()))
                    else -> null
                }
            } else {
                null
            }
        }
    }
    install(KoinPlugin) {
        appDeclaration = {
            slf4jLogger(level = Level.INFO)
            modules(appModule)
        }
    }
    install(Sessions) {
        apiSessionCookie(isDevelopment)
    }
    if (useCsrf) {
        install(CsrfPlugin) {
            validateHeader("X-CSRF") { it.call.getCsrfToken() }
        }
    }
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            when (cause) {
                is IdNotFoundException -> call.respondText(
                    "file Id [${cause.id}] not found",
                    status = HttpStatusCode.NotFound
                )
                is CsrfInvalidException -> call.respond(HttpStatusCode.Forbidden)
                is java.nio.file.FileAlreadyExistsException -> call.respondText(
                    "file already exists: ${cause.message}",
                    status = HttpStatusCode.InternalServerError
                )
                else -> {
                    cause.printStackTrace()
//                    call.respondText(
//                        cause.message.orEmpty(),
//                        status = HttpStatusCode.InternalServerError
//                    )
                }
            }
        }
    }

}

fun Application.routes() {
    val env = environment.config.property("ktor.environment").getString()

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

