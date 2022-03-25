package uk.dioxic.muon.api

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.browser.window
import uk.dioxic.muon.*
import uk.dioxic.muon.audio.AudioDetails
import uk.dioxic.muon.audio.AudioFile
import uk.dioxic.muon.audio.ImportError
import uk.dioxic.muon.config.LibraryConfig
import uk.dioxic.muon.config.Settings


object Api {

    val endpoint = window.location.origin // only needed until https://github.com/ktorio/ktor/issues/1695 is resolved
    val configEndpoint = endpoint + settingsPath
    val client = HttpClient {
        install(JsonFeature) { serializer = KotlinxSerializer() }
        defaultRequest {
            port = 8080
        }
    }

//suspend fun getImportFiles(libraryId: String, refresh: Boolean = false): List<AudioDetails> =
//    client.get(path = importPath) {
//        parameter("refresh", refresh)
//        parameter("library", libraryId)
//    }
//
//suspend fun getAudioDetails(libraryId: String, refresh: Boolean = false): List<AudioFile> =
//    client.get<List<AudioFileMatch>>(path = musicPath) {
//        parameter("refresh", refresh)
//        parameter("library", libraryId)
//    }.map { it.audioFile }

    suspend fun searchAudio(
        libraryId: String? = null,
        text: String? = null,
        includeDuplicates: Boolean = false,
        refresh: Boolean = false,
        sortField: String? = null,
        sortReverse: Boolean? = null
    ): List<AudioDetails> =
        client.get(path = musicPath) {
            text.nullIfBlank()?.also { parameter("q", it) }
            libraryId?.also { parameter("library", it) }
            sortField?.also { parameter("sort", it.lowercase()) }
            sortReverse?.also { parameter("sortReverse", it) }
            parameter("includeDuplicates", includeDuplicates)
            parameter("refresh", refresh)
        }

    suspend fun getLibraryConfig(): LibraryConfig =
        client.get {
            url {
                path(settingsPath, libraryPath)
            }
        }

    suspend fun saveAudioFiles(libraryId: String? = null, files: List<AudioFile>) =
        client.patch<List<ImportError>>(
            path = musicPath,
            body = files
        ) {
            contentType(ContentType.Application.Json)
            parameter("library", libraryId)
        }

    suspend fun deleteAudioFile(file: AudioFile) {
        return client.delete {
            url {
                path(musicPath, file.id)
            }
            contentType(ContentType.Application.Json)
        }
    }

    suspend fun getSettings(): Settings =
        client.get(path = settingsPath)


    suspend fun saveSettings(settings: Settings) {
        return client.post(path = settingsPath) {
            contentType(ContentType.Application.Json)
            body = settings
        }
    }

    suspend fun addShoppingListItem(shoppingListItem: ShoppingListItem) {
        client.post<Unit> {
            url {
                path(shoppingListPath)
            }
            contentType(ContentType.Application.Json)
            body = shoppingListItem
        }
    }

    suspend fun deleteShoppingListItem(shoppingListItem: ShoppingListItem) {
        client.delete<Unit> {
            url {
                path(shoppingListPath, shoppingListItem.id.toString())
            }
        }
    }

    suspend fun getShoppingList(): List<ShoppingListItem> =
        client.get(path = shoppingListPath)

}