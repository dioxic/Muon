package uk.dioxic.muon

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import uk.dioxic.muon.config.Settings
import uk.dioxic.muon.repository.RekordboxRepository
import uk.dioxic.muon.repository.SettingsRepository
import uk.dioxic.muon.route.Routes
import uk.dioxic.muon.service.MusicService
import kotlin.io.path.ExperimentalPathApi

fun Routing.tracks() {
    val musicService by inject<MusicService>()
    val rekordboxRepository by inject<RekordboxRepository>()

    route(Routes.track) {
        get("/{id}") {
            val id = call.parameters["id"]
            call.respond(rekordboxRepository.getTrackById(id!!))
        }
        get {
            val maxResults = call.parameters["maxResults"]?.toIntOrNull() ?: 500
            val query = call.parameters["q"]
            call.respond(musicService.search(query, maxResults))
        }
    }
}

fun Routing.lucene() {
    val musicService by inject<MusicService>()

    route(Routes.index) {
        get("/rebuild") {
            val count = musicService.rebuildIndex()
            call.respond("rebuilt index for $count tracks")
        }
        get("/refresh") {
            val count = musicService.refreshIndex()
            call.respond("refreshed index for $count tracks")
        }
    }
}

@ExperimentalPathApi
fun Routing.settings() {
    val settingsRepository by inject<SettingsRepository>()

    route(Routes.settings) {
        get {
            call.respond(settingsRepository.get())
        }
        post {
            settingsRepository.save(call.receive() as Settings)
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


// TODO remove when koin is compatible with ktor 2.0.0

private inline fun <reified T : Any> Routing.inject(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
) =
    lazy { get<T>(qualifier, parameters) }

private inline fun <reified T : Any> Routing.get(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
) =
    getKoin().get<T>(qualifier, parameters)

fun Routing.getKoin() = GlobalContext.get()