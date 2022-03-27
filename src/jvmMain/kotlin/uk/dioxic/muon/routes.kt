package uk.dioxic.muon

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.flow.toList
import org.koin.ktor.ext.inject
import uk.dioxic.muon.repository.SettingsRepository
import uk.dioxic.muon.config.Settings
import uk.dioxic.muon.repository.*
import uk.dioxic.muon.route.Routes
import uk.dioxic.muon.service.MusicService
import kotlin.io.path.ExperimentalPathApi

fun Routing.search() {
    val luceneRepository by inject<LuceneRepository>()
    val rekordboxRepository by inject<RekordboxRepository>()

    route(Routes.search) {
        get {
            val maxResults = call.parameters["maxResults"]?.toIntOrNull() ?: 500
            val query = call.parameters["q"]
            val trackIds = luceneRepository.search(query, maxResults)
            val tracks = rekordboxRepository.getRekordboxTracksById(trackIds)
            call.respond(tracks.toList(mutableListOf()))
        }
    }
}

fun Routing.lucene() {
    val musicService by inject<MusicService>()
//    val luceneRepository by inject<LuceneRepository>()

    route(Routes.index) {
        get("/rebuild") {
            val count = musicService.buildIndex()
            call.respond("rebuilt index for $count tracks")
        }
        get("/refresh") {
            val count = musicService.refreshIndex()
            call.respond("refreshed index for $count tracks")
        }
//        get("/drop") {
//            luceneRepository.dropIndex()
//            call.respond(HttpStatusCode.OK)
//        }
    }
}

@ExperimentalPathApi
fun Routing.settings() {

    val settingsRepository by inject<SettingsRepository>()

    route(Routes.settings) {
//        subconfig(
//            key = AudioImportConfig.path,
//            getFn = { configRepository.getImportConfig() },
//            setFn = { configRepository.save(it) }
//        )
//        subconfig(
//            key = LibraryConfig.path,
//            getFn = { configRepository.getLibraryConfig() },
//            setFn = { configRepository.save(it) }
//        )

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