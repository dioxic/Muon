package uk.dioxic.muon.config

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val importTableColumns: List<TableColumnConfig>,
    val theme: String,
    val importPath: String? = null,
    val rekordboxDatabase: String? = null,
    val lastRekordboxRefresh: LocalDateTime,
) {
    companion object {
        val DEFAULT = Settings(
            importTableColumns = listOf(
                TableColumnConfig(id = "title", label = "Title", minWidth = 170),
                TableColumnConfig(id = "artist", label = "Artist", minWidth = 170),
                TableColumnConfig(id = "lyricist", label = "Lyricist", minWidth = 170),
                TableColumnConfig(id = "genre", label = "Genre", minWidth = 170, visible = false),
                TableColumnConfig(id = "comment", label = "Comment", minWidth = 170, visible = false),
                TableColumnConfig(id = "bitrate", label = "Bitrate", minWidth = 100),
                TableColumnConfig(id = "type", label = "Type", minWidth = 100),
                TableColumnConfig(id = "path", label = "Path", minWidth = 170, visible = false),
                TableColumnConfig(id = "filename", label = "Filename", minWidth = 170),
                TableColumnConfig(id = "length", label = "Length", minWidth = 100),
                TableColumnConfig(id = "year", label = "Year", minWidth = 100),
            ),
            theme = "dark",
            rekordboxDatabase = "J:\\rekordbox\\master.db", //TODO remove this
            importPath = "J:\\import\\complete",  //TODO remove this
            lastRekordboxRefresh = LocalDateTime(1985, 1, 1, 0, 0, 0),
        )
    }
}