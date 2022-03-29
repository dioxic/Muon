@file:OptIn(ExperimentalSerializationApi::class)
@file:Suppress("EXPERIMENTAL_IS_NOT_ENABLED")

package uk.dioxic.muon.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.browser.window
import kotlinx.serialization.ExperimentalSerializationApi
import uk.dioxic.muon.utils.CsrfTokenHandler

val client = HttpClient(Js) {
    install(ContentNegotiation) {
        json()
    }
    defaultRequest {
        port = 8080
    }
}

fun HttpRequestBuilder.localUrl(path: String) = url {
    takeFrom(window.location.href)
    encodedPath = path
}

suspend inline fun <reified T> apiRequest(requestConfigurator: HttpRequestBuilder.() -> Unit): T {
    val res = client.request {
        requestConfigurator()
        if (method != HttpMethod.Get) {
            header("X-CSRF", CsrfTokenHandler.getToken())
        }
    }
    return res.body()
}

object Api {

    suspend inline fun <reified T> get(path: String): T =
        apiRequest {
            method = HttpMethod.Get
            localUrl(path)
        }

    suspend inline fun <reified T> rawPost(path: String, noinline formBuilder: FormBuilder.() -> Unit): T =
        apiRequest {
            method = HttpMethod.Post
            localUrl(path)
            formData(formBuilder)
        }

    suspend inline fun <reified T> post(path: String, data: Any): T =
        apiRequest {
            method = HttpMethod.Post
            localUrl(path)
            contentType(ContentType.Application.Json)
            setBody(data)
        }

}