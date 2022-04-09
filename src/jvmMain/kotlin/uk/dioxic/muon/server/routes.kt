package uk.dioxic.muon.server

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import kotlinx.html.*
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import uk.dioxic.muon.model.SettingsLoadResponse
import uk.dioxic.muon.repository.RekordboxRepository
import uk.dioxic.muon.repository.SettingsRepository
import uk.dioxic.muon.route.Routes
import uk.dioxic.muon.service.ImportService
import uk.dioxic.muon.service.SearchService
import java.io.File

fun Routing.tracks() {
    val searchService by inject<SearchService>()
    val rekordboxRepository by inject<RekordboxRepository>()

    route(Routes.track) {
        get("/{id}") {
            val id = call.parameters["id"]
            call.respond(rekordboxRepository.getTrackById(id!!))
        }
        get {
            val maxResults = call.request.queryParameters["maxResults"]?.toIntOrNull() ?: 500
            val query = call.request.queryParameters["q"]
            call.respond(searchService.search(query, maxResults))
        }
    }
}

fun Routing.lucene() {
    val searchService by inject<SearchService>()

    route(Routes.index) {
        get("/rebuild") {
            val count = searchService.rebuildIndex()
            call.respond("rebuilt index for $count tracks")
        }
        get("/refresh") {
            val count = searchService.refreshIndex()
            call.respond("refreshed index for $count tracks")
        }
    }
}

fun Routing.settings() {
    val settingsRepository by inject<SettingsRepository>()

    route(Routes.settings) {
        get {
            call.respond(SettingsLoadResponse(settingsRepository.get()))
        }
        put {
            settingsRepository.save(call.receive())
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Routing.import() {
    val importService by inject<ImportService>()

    route(Routes.import) {
        get {
            val tracks = importService.getTracks()
            call.updateApiSession { currentSession ->
                currentSession.copy(importFileLocations = tracks.associate { it.id to it.path.encodeBase64() })
            }
            call.respond(tracks)
        }
        get("/{id}") {
            val session = call.getUserSession()
            val trackId = call.parameters["id"]
            val trackFile = session.importFileLocations[trackId]?.decodeBase64String()

            if (trackFile != null) {
                call.respond(importService.getTrack(File(trackFile)))
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
        patch("/{id}") {
            val session = call.getUserSession()
            val trackId = call.parameters["id"]
            val trackFile = session.importFileLocations[trackId]?.decodeBase64String()

            if (trackFile != null) {
                call.respond(importService.updateTrack(File(trackFile), call.receive()))
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
        get("/retrieve") {
            val userSession = call.getUserSession()
            if (userSession.importFileLocations.isNotEmpty()) {
                val decodedImportFiles = userSession.importFileLocations.mapValues { (_, v) -> v.decodeBase64String() }
                call.respondText("Import files: $decodedImportFiles")
            } else {
                call.respondText("Your import list is empty.")
            }
        }
    }
}

fun Routing.indexHtml() {
    get("/") {
        val token = call.getCsrfToken()
        call.respondHtml {
            head {
                meta { charset = Charsets.UTF_8.name() }
                meta {
                    name = "viewport"
                    content = "initial-scale=1, width=device-width"
                }
                title { +"Muon" }
                link {
                    rel = LinkRel.stylesheet
                    href = "index.css"
                    type = "text/css"
                }
                link {
                    rel = "shortcut icon"
                    href = "favicon.ico"
                }
                link {
                    rel = LinkRel.stylesheet
                    href = "https://fonts.googleapis.com/css?family=Roboto:300,400,500,700&display=swap"
                }
            }
            body {
                hiddenInput {
                    id = "_csrf_token"
                    value = token
                }
                div { id = "root" }
                noScript {
                    +"You need to enable JavaScript to run this app."
                }
                script {
                    type = ScriptType.textJavaScript
                    src = "/app.js"
                }
            }
        }
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