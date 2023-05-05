package uk.dioxic.muon.server

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.html.*
import org.koin.ktor.ext.inject
import uk.dioxic.muon.Routes
import uk.dioxic.muon.common.validate
import uk.dioxic.muon.config.Settings
import uk.dioxic.muon.model.ImportResponse
import uk.dioxic.muon.model.SettingsResponse
import uk.dioxic.muon.model.Track
import uk.dioxic.muon.model.Tracks
import uk.dioxic.muon.repository.SettingsRepository
import uk.dioxic.muon.service.SearchService
import uk.dioxic.muon.service.TrackService
import java.io.File

fun Routing.tracks() {
    val searchService by inject<SearchService>()
    val trackService by inject<TrackService>()

    route(Routes.track) {
        get("/{id}") {
            val id = call.parameters["id"]
            trackService.getTrackById(id!!)?.let {
                call.respond(it)
            } ?: call.respond(HttpStatusCode.NotFound)
        }
        get("/{id}/audio") {
            val id = call.parameters["id"]
            trackService.getTrackById(id!!)?.path?.let { path ->
                val audioFile = File(path)
                call.response.header(
                    name = HttpHeaders.ContentDisposition,
                    value = ContentDisposition.Attachment
                        .withParameter(ContentDisposition.Parameters.FileName, audioFile.name)
                        .withParameter(ContentDisposition.Parameters.Name, audioFile.name)
                        .toString()
                )
                call.respondFile(audioFile)
            } ?: call.respond(HttpStatusCode.NotFound)

        }
        get {
            val maxResults = call.request.queryParameters["maxResults"]?.toIntOrNull() ?: 100
            val query = call.request.queryParameters["q"]
            call.respond(searchService.search(query, maxResults))
        }
        get("trackSearch") {
            val maxResults = call.request.queryParameters["maxResults"]?.toIntOrNull() ?: 100
            val track = Track.EMPTY.copy(
                artist = call.request.queryParameters["artist"] ?: "",
                title = call.request.queryParameters["title"] ?: "",
                lyricist = call.request.queryParameters["lyricist"] ?: "",
            )

            call.respond(searchService.search(track, maxResults))
        }
        post("trackSearch") {
            val maxResults = call.request.queryParameters["maxResults"]?.toIntOrNull() ?: 100
            val track = call.receive<Track>()
            call.respond(searchService.search(track, maxResults))
        }
        patch {
            call.respond(trackService.updateTrack(call.receive()))
        }
        delete("/{id}") {
            trackService.deleteTrack(call.parameters["id"]!!)
            call.respond(HttpStatusCode.OK)
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
            val settings = settingsRepository.get()
            call.respond(
                SettingsResponse(settings, settings.validate())
            )
        }
        put {
            val settings = call.receive<Settings>()
            call.respond(
                SettingsResponse(
                    settings = settingsRepository.save(settings),
                    errors = settings.validate()
                )
            )
        }
    }
}

fun Routing.import() {
    val trackService by inject<TrackService>()
    val searchService by inject<SearchService>()

    route(Routes.import) {
        get {
            var tracks = trackService.getImportTracks()

            if (!call.request.queryParameters["exDuplicates"].toBoolean()) {
                tracks = tracks.map { track ->
                    val duplicates = searchService.search(track, 5)
                    if (duplicates.isNotEmpty()) {
                        track.copy(duplicates = duplicates)
                    } else {
                        track
                    }
                }
            }
            call.respond(tracks)
        }
        post {
            val tracks = call.receive<Tracks>()
            val successes = mutableListOf<String>()
            val errors = mutableMapOf<String, String>()

            tracks.forEach { track ->
                withContext(Dispatchers.IO) {
                    try {
                        trackService.importTrack(track.id)
                        successes.add(track.id)
                    } catch (e: Throwable) {
                        errors[track.id] = "${e::class.simpleName} - ${e.message}"
                    }
                }
            }
            call.respond(message = ImportResponse(successes, errors))
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