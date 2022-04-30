package uk.dioxic.muon.server

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import uk.dioxic.muon.common.Global
import uk.dioxic.muon.server.plugins.CsrfTokenProvider
import java.io.File
import kotlin.time.Duration.Companion.days

typealias FileLocations = Map<String, String>

data class UserSession(
    val currentUser: String = "anonymous",
    val csrfToken: String = CsrfTokenProvider.generateRandomToken(),
    val importFileLocations: FileLocations = emptyMap()
)

fun SessionsConfig.apiSessionCookie(memoryStorage: Boolean) {
    val secretSignKey = hex("6819b57a326945c1968f45236589")
    val sessionStorage = if (memoryStorage) {
        SessionStorageMemory()
    } else {
        directorySessionStorage(File("${Global.configPath}/.sessions"))
    }
    cookie<UserSession>("session", sessionStorage) {
        transform(SessionTransportTransformerMessageAuthentication(secretSignKey))
        cookie.maxAgeInSeconds = 1.days.inWholeSeconds
    }
}

fun ApplicationCall.getUserSession(): UserSession = sessions.getOrSet { UserSession() }
fun ApplicationCall.getCsrfToken(): String = getUserSession().csrfToken
fun ApplicationCall.setApiSession(session: UserSession) = sessions.set(session)
fun ApplicationCall.updateApiSession(callback: (UserSession) -> UserSession) = sessions.set(callback(getUserSession()))
fun ApplicationCall.deleteApiSession() = sessions.clear<UserSession>()