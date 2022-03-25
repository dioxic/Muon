package uk.dioxic.muon.config

import kotlinx.serialization.Serializable
import uk.dioxic.muon.model.Library

@Serializable
data class Settings(
    val importTableColumns: List<TableColumnConfig>,
    val libraries: List<Library>,
    val theme: String,
){
    companion object {
        val DEFAULT = Settings(
            importTableColumns = defaultImportTableColumns,
            libraries = emptyList(),
            theme = "dark"
        )
    }
}

private val defaultImportTableColumns = listOf(
    TableColumnConfig(id = "title", label = "Title", minWidth = 170),
    TableColumnConfig(id = "artist", label = "Artist", minWidth = 170),
    TableColumnConfig(id = "lyricist", label = "Lyricist", minWidth = 170),
    TableColumnConfig(id = "genre", label = "Genre", minWidth = 170, visible = false),
    TableColumnConfig(id = "comment", label = "Comment", minWidth = 170, visible = false),
    TableColumnConfig(id = "bitrate", label = "Bitrate", minWidth = 100),
    TableColumnConfig(id = "vbr", label = "VBR", minWidth = 100, visible = false, align = "center"),
    TableColumnConfig(id = "type", label = "Type", minWidth = 100),
    TableColumnConfig(id = "path", label = "Path", minWidth = 170, visible = false),
    TableColumnConfig(id = "filename", label = "Filename", minWidth = 170),
    TableColumnConfig(id = "length", label = "Length", minWidth = 100),
    TableColumnConfig(id = "year", label = "Year", minWidth = 100),
)