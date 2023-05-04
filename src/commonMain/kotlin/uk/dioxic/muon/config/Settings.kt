package uk.dioxic.muon.config

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val theme: String,
    val downloadDirs: List<String> = emptyList(),
    val importDir: String = "",
    val rekordboxDatabase: String = "",
    val lastRekordboxRefresh: LocalDateTime,
    val folderMappings: List<Pair<String,String>> = emptyList(),
    val softDelete: Boolean = true,
    val deleteDir: String = "",
    val standardiseFilenames: Boolean = true,
) {
    companion object {
        val DEFAULT = Settings(
            theme = "dark",
            lastRekordboxRefresh = LocalDateTime(1985, 1, 1, 0, 0, 0),
        )
    }
}