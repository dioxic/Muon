package uk.dioxic.muon.server.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import uk.dioxic.muon.exceptions.CsrfInvalidException
import java.security.SecureRandom
import java.util.*

typealias CsrfValidator = (ApplicationRequest) -> Boolean

val CsrfPlugin = createApplicationPlugin(
    name = "Csrf",
    ::CsrfPluginConfiguration
) {
    onCallReceive { call, _ ->
        pluginConfig.apply {
            if (call.request.httpMethod != HttpMethod.Get) {
                if (validators.any { !it(call.request) }) {
                    throw CsrfInvalidException("token not recognoised")
                }
            }
        }
    }
}

class CsrfPluginConfiguration {
    internal var validators: MutableList<CsrfValidator> = mutableListOf()

    private fun validator(validator: CsrfValidator) {
        this.validators.add(validator)
    }

    fun validateHeader(headerName: String, calculateExpectedContent: (ApplicationRequest) -> String?) {
        validator { request ->
            calculateExpectedContent(request)
                ?.let { request.headers[headerName] == it }
                ?: true
        }
    }
}

object CsrfTokenProvider {
    private val secureRandom = SecureRandom()

    fun generateRandomToken(): String =
        ByteArray(256)
            .also { secureRandom.nextBytes(it) }
            .let { Base64.getEncoder().encodeToString(it) }
}