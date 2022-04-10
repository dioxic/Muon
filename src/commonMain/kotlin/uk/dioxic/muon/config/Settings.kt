package uk.dioxic.muon.config

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val theme: String,
    val importPath: String? = null,
    val rekordboxDatabase: String? = null,
    val lastRekordboxRefresh: LocalDateTime,
    val softDelete: Boolean = true,
    val deletePath: String? = null
) {
    companion object {
        val DEFAULT = Settings(
            theme = "dark",
            rekordboxDatabase = "J:\\rekordbox\\master.db", //TODO remove this
            importPath = "J:\\import\\complete",  //TODO remove this
            deletePath = "J:\\import\\deleted",
            lastRekordboxRefresh = LocalDateTime(1985, 1, 1, 0, 0, 0),
        )
    }
}