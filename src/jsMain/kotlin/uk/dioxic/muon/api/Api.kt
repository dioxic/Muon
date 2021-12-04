package uk.dioxic.muon.api

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.browser.window
import kotlinx.html.MetaHttpEquiv.refresh
import uk.dioxic.muon.*
import uk.dioxic.muon.audio.AudioFile
import uk.dioxic.muon.config.AudioImportConfig

val endpoint = window.location.origin // only needed until https://github.com/ktorio/ktor/issues/1695 is resolved
val configEndpoint = endpoint + configPath

val jsonClient = HttpClient {
    install(JsonFeature) { serializer = KotlinxSerializer() }
}

suspend fun getShoppingList(): List<ShoppingListItem> {
    return jsonClient.get(endpoint + shoppingListPath)
}

//suspend fun getAudioLibraryList(): List<AudioFile> {
//    return jsonClient.get(endpoint + AudioFile.path)
//}

suspend fun getAudioImportList(reload: Boolean = false): List<AudioFile> {
    return jsonClient.get(endpoint + importPath) {
        parameter("reload", reload)
    }
}

suspend fun getAudioImportConfig(): AudioImportConfig {
    return jsonClient.get(configEndpoint + AudioImportConfig.path)
}

suspend fun saveAudioFile(file: AudioFile) {
    return jsonClient.post(endpoint + importPath) {
        console.log("posting audio")
        contentType(ContentType.Application.Json)
        body = file
    }
}

suspend fun fetchFullConfig(): ConfigMap {
    return jsonClient.get(configEndpoint)
}

suspend fun saveAudioImportConfig(config: AudioImportConfig) {
    return jsonClient.post(configEndpoint + AudioImportConfig.path) {
        contentType(ContentType.Application.Json)
        body = config
    }
}

suspend fun addShoppingListItem(shoppingListItem: ShoppingListItem) {
    jsonClient.post<Unit>(endpoint + shoppingListPath) {
        contentType(ContentType.Application.Json)
        body = shoppingListItem
    }
}

suspend fun deleteShoppingListItem(shoppingListItem: ShoppingListItem) {
    jsonClient.delete<Unit>(endpoint + shoppingListPath + "/${shoppingListItem.id}")
}