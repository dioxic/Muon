package uk.dioxic.muon.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.browser.window
import uk.dioxic.muon.utils.CsrfTokenHandler

class InternalServerException(
    response: HttpResponse,
    override val message: String
) : ResponseException(response, message)

val client = HttpClient(Js) {
    install(ContentNegotiation) {
        json()
    }
    defaultRequest {
        port = 8080
    }
    expectSuccess = true
    HttpResponseValidator {
        handleResponseExceptionWithRequest { exception, _ ->
            // the default response validator only throw ResponseExceptions
            val responseException = exception as? ResponseException ?: return@handleResponseExceptionWithRequest

            with(responseException.response) {
                when (status) {
                    HttpStatusCode.InternalServerError -> throw InternalServerException(this, bodyAsText())
                }
            }
        }
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

    suspend inline fun <reified T> get(path: String, vararg parameters: Pair<String, String>): T =
        apiRequest {
            method = HttpMethod.Get
            localUrl(path)
            parameters.forEach { (k, v) ->
                parameter(k, v)
            }
        }

    suspend inline fun <reified T> rawPost(path: String, noinline formBuilder: FormBuilder.() -> Unit): T =
        apiRequest {
            method = HttpMethod.Post
            localUrl(path)
            formData(formBuilder)
        }

    suspend inline fun <reified T, reified TResult> post(
        path: String,
        data: T,
        vararg parameters: Pair<String, String>
    ): TResult =
        apiRequest {
            method = HttpMethod.Post
            localUrl(path)
            parameters.forEach { (k, v) ->
                parameter(k, v)
            }

            contentType(ContentType.Application.Json)
            setBody(data)
        }

    suspend inline fun <reified T, reified TResult> put(path: String, data: T): TResult =
        apiRequest {
            method = HttpMethod.Put
            localUrl(path)
            contentType(ContentType.Application.Json)
            setBody(data)
        }

    suspend inline fun <reified T> patch(path: String, data: T): T =
        apiRequest {
            method = HttpMethod.Patch
            localUrl(path)
            contentType(ContentType.Application.Json)
            setBody(data)
        }

    suspend inline fun delete(path: String): Unit =
        apiRequest {
            method = HttpMethod.Delete
            localUrl(path)
            contentType(ContentType.Application.Json)
        }

}