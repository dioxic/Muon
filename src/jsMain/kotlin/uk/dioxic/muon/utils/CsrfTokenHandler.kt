package uk.dioxic.muon.utils

import kotlinx.browser.document
import org.w3c.dom.HTMLInputElement

object CsrfTokenHandler {
    private val csrfTokenInputElement
        get() = document.getElementById("_csrf_token")
            .let { it as HTMLInputElement }

    fun setToken(token: String) { csrfTokenInputElement.value = token }

    fun getToken(): String = csrfTokenInputElement.value
}