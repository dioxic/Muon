package uk.dioxic.muon.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.browser.window
import kotlinx.serialization.json.Json
import uk.dioxic.muon.config.Settings
import uk.dioxic.muon.route.Routes


object Api {

    val endpoint = window.location.origin // only needed until https://github.com/ktorio/ktor/issues/1695 is resolved
    private val client = HttpClient(Js) {
        install(ContentNegotiation) { json(Json) }
        defaultRequest {
            port = 8080
        }
    }

    suspend fun getSettings(): Settings =
        client.get(Routes.settings).body()


    suspend fun saveSettings(settings: Settings) =
        client.post(Routes.settings) {
            contentType(ContentType.Application.Json)
            setBody(settings)
        }

}