package uk.dioxic.muon.config

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val importTableColumns: List<ColumnDefinition>,
    val theme: String,
    val importPath: String? = null,
    val rekordboxDatabase: String? = null,
    val lastRekordboxRefresh: LocalDateTime,
) {
    companion object {
        val DEFAULT = Settings(
            importTableColumns = listOf(
                ColumnDefinition(id = "title", label = "Title", minWidth = 170),
                ColumnDefinition(id = "artist", label = "Artist", minWidth = 170),
                ColumnDefinition(id = "lyricist", label = "Lyricist", minWidth = 170),
                ColumnDefinition(id = "genre", label = "Genre", minWidth = 170, visible = false),
                ColumnDefinition(id = "comment", label = "Comment", minWidth = 170, visible = false),
                ColumnDefinition(id = "bitrate", label = "Bitrate", minWidth = 100),
                ColumnDefinition(id = "type", label = "Type", minWidth = 100),
                ColumnDefinition(id = "path", label = "Path", minWidth = 170, visible = false),
                ColumnDefinition(id = "filename", label = "Filename", minWidth = 170),
                ColumnDefinition(id = "length", label = "Length", minWidth = 100),
                ColumnDefinition(id = "year", label = "Year", minWidth = 100),
            ),
            theme = "dark",
            rekordboxDatabase = "J:\\rekordbox\\master.db", //TODO remove this
            importPath = "J:\\import\\complete",  //TODO remove this
            lastRekordboxRefresh = LocalDateTime(1985, 1, 1, 0, 0, 0),
        )
    }
}