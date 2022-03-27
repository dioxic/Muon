package uk.dioxic.muon.api

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.browser.window
import uk.dioxic.muon.config.Settings
import uk.dioxic.muon.route.Routes


object Api {

    val endpoint = window.location.origin // only needed until https://github.com/ktorio/ktor/issues/1695 is resolved
    private val client = HttpClient {
        install(JsonFeature) { serializer = KotlinxSerializer() }
        defaultRequest {
            port = 8080
        }
    }

    suspend fun getSettings(): Settings =
        client.get(path = Routes.settings)


    suspend fun saveSettings(settings: Settings) {
        return client.post(path = Routes.settings) {
            contentType(ContentType.Application.Json)
            body = settings
        }
    }

}