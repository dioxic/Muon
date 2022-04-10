package uk.dioxic.muon.config

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val theme: String,
    val downloadDirs: List<String> = emptyList(),
    val importDir: String? = null,
    val rekordboxDatabase: String? = null,
    val lastRekordboxRefresh: LocalDateTime,
    val softDelete: Boolean = true,
    val deleteDir: String? = null,
    val standardiseFilenames: Boolean = true
) {
    companion object {
        val DEFAULT = Settings(
            theme = "dark",
            rekordboxDatabase = "J:\\rekordbox\\master.db", //TODO remove this
            downloadDirs = listOf("J:\\import\\complete"),  //TODO remove this
            deleteDir = "J:\\import\\deleted",  //TODO remove this
            lastRekordboxRefresh = LocalDateTime(1985, 1, 1, 0, 0, 0),
        )
    }
}