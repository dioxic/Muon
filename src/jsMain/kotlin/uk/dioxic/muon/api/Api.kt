package uk.dioxic.muon.api

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.browser.window
import uk.dioxic.muon.*
import uk.dioxic.muon.audio.AudioFile
import uk.dioxic.muon.audio.ImportError
import uk.dioxic.muon.config.AudioImportConfig
import uk.dioxic.muon.config.LibraryConfig
import uk.dioxic.muon.model.ConfigMap

val endpoint = window.location.origin // only needed until https://github.com/ktorio/ktor/issues/1695 is resolved
val configEndpoint = endpoint + configPath

val client = HttpClient {
    install(JsonFeature) { serializer = KotlinxSerializer() }
    defaultRequest {
        port = 8080
    }
}

suspend fun getAudioFiles(libraryId: String, refresh: Boolean = false): List<AudioFile> =
    client.get(path = musicPath) {
        parameter("refresh", refresh)
        parameter("library", libraryId)
    }

suspend fun getImportConfig(): AudioImportConfig =
    client.get {
        url {
            path(configPath, importPath)
        }
    }

suspend fun getLibraryConfig(): LibraryConfig =
    client.get {
        url {
            path(configPath, libraryPath)
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

suspend fun fetchFullConfig(): ConfigMap =
    client.get(path = configPath)

suspend fun saveLibraryConfig(config: AudioImportConfig) {
    return client.post {
        url {
            path(configPath, AudioImportConfig.path)
        }
        contentType(ContentType.Application.Json)
        body = config
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
