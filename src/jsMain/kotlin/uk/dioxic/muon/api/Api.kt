package uk.dioxic.muon.api

import io.ktor.http.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer

import kotlinx.browser.window
import uk.dioxic.muon.audio.AudioFileImport
import uk.dioxic.muon.config.AudioImportConfig
import uk.dioxic.muon.ConfigMap
import uk.dioxic.muon.ShoppingListItem

val endpoint = window.location.origin // only needed until https://github.com/ktorio/ktor/issues/1695 is resolved
val configEndpoint = endpoint + ConfigMap.path

val jsonClient = HttpClient {
    install(JsonFeature) { serializer = KotlinxSerializer() }
}

suspend fun getShoppingList(): List<ShoppingListItem> {
    return jsonClient.get(endpoint + ShoppingListItem.path)
}

//suspend fun getAudioLibraryList(): List<AudioFile> {
//    return jsonClient.get(endpoint + AudioFile.path)
//}

suspend fun getAudioImportList(): List<AudioFileImport> {
    return jsonClient.get(endpoint + AudioFileImport.path)
}

suspend fun getAudioImportConfig(): AudioImportConfig {
    return jsonClient.get(configEndpoint + AudioImportConfig.path)
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
    jsonClient.post<Unit>(endpoint + ShoppingListItem.path) {
        contentType(ContentType.Application.Json)
        body = shoppingListItem
    }
}

suspend fun deleteShoppingListItem(shoppingListItem: ShoppingListItem) {
    jsonClient.delete<Unit>(endpoint + ShoppingListItem.path + "/${shoppingListItem.id}")
}