package uk.dioxic.muon.server

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.*
import java.security.SecureRandom
import java.util.*

typealias CsrfValidator = (ApplicationRequest) -> Boolean

internal class Csrf(config: CsrfConfiguration) {
    private val validators: List<CsrfValidator> = config.validators.toList()

    class CsrfConfiguration {
        internal var validators: MutableList<CsrfValidator> = mutableListOf()

        fun validator(validator: CsrfValidator) {
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

    companion object Plugin : ApplicationPlugin<ApplicationCallPipeline, CsrfConfiguration, Csrf> {
        override val key = AttributeKey<Csrf>("Csrf")

        override fun install(pipeline: ApplicationCallPipeline, configure: CsrfConfiguration.() -> Unit): Csrf {
            val csrf = Csrf(CsrfConfiguration().apply(configure))
            pipeline.intercept(ApplicationCallPipeline.Plugins) {
                if (call.request.httpMethod != HttpMethod.Get) {
                    if (csrf.validators.any { !it(call.request) }) {
                        call.respond(HttpStatusCode.Forbidden)
                        finish()
                    }
                }
            }
            return csrf
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