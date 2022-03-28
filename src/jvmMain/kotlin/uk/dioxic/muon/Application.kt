@file:Suppress("unused")

package uk.dioxic.muon

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import kotlinx.coroutines.FlowPreview
import kotlinx.serialization.json.Json
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
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
import uk.dioxic.muon.service.SearchService
import kotlin.io.path.ExperimentalPathApi
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
}

fun main(args: Array<String>) {
    System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager")
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}

@FlowPreview
@ExperimentalTime
@ExperimentalPathApi
fun Application.main() {
    val env = environment.config.property("ktor.environment").getString()

    install(CallLogging)
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = env == "dev"
            encodeDefaults = false
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
    install(CustomKoinPlugin) {
        slf4jLogger(level = Level.ERROR)
        modules(appModule)
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
        index()
        tracks()
        lucene()

        static("/") {
            resources("static")
        }

        get("/env") {
            call.respondText(env)
        }
    }
}

internal class CustomKoinPlugin(internal val koinApplication: KoinApplication) {
    // Implements ApplicationPlugin as a companion object.
    companion object Plugin : ApplicationPlugin<ApplicationCallPipeline, KoinApplication, CustomKoinPlugin> {
        // Creates a unique key for the plugin.
        override val key = AttributeKey<CustomKoinPlugin>("CustomKoinPlugin")

        // Code to execute when installing the plugin.
        override fun install(
            pipeline: ApplicationCallPipeline,
            configure: KoinApplication.() -> Unit
        ): CustomKoinPlugin {
            val koinApplication = startKoin(appDeclaration = configure)
            return CustomKoinPlugin(koinApplication)
        }
    }
}