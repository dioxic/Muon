package uk.dioxic.muon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import uk.dioxic.muon.AudioImportFieldKey.*
import uk.dioxic.muon.ConfigKey.*

enum class ConfigKey { AudioImport }

@Serializable
data class Config(
    val audioImportConfig: AudioImportConfig
) {

    operator fun get(key: ConfigKey) =
        when (key) {
            AudioImport -> audioImportConfig
        }

    companion object {
        const val path = "/config"
        val Default = Config(
            audioImportConfig = AudioImportConfig.Default
        )
    }
}

@Serializable
data class AudioImportConfig(
    val columns: Map<AudioImportFieldKey, Column>
) {
    companion object {
        val path = "${Config.path}/${AudioImport.name}"
        val Default = AudioImportConfig(
            linkedMapOf(
                OriginalArtist to Column(label = "Original Artist", disablePadding = true, visible = false),
                StandardizedArtist to Column(label = "Artist", visible = true),
                OriginalTitle to Column(label = "Original Title", visible = false),
                StandardizedTitle to Column(label = "Title", visible = true),
                OriginalGenre to Column(label = "Original Genre", visible = false),
                StandardizedGenre to Column(label = "Genre", visible = true),
                OriginalComment to Column(label = "Original Comment", visible = false),
                StandardizedComment to Column(label = "Comment", visible = true),
                OriginalLyricist to Column(label = "Original Lyricist", visible = false),
                StandardizedLyricist to Column(label = "Lyricist", visible = true),
                OriginalYear to Column(label = "Original Year", rightAligned = true, visible = false),
                StandardizedYear to Column(label = "Year", rightAligned = true, visible = true),
                OriginalAlbum to Column(label = "Original Album", visible = false),
                StandardizedAlbum to Column(label = "Album", visible = true),
                OriginalFilename to Column(label = "Original Filename", visible = true),
                StandardizedFilename to Column(label = "Filename", visible = true),
                Length to Column(label = "Length", rightAligned = true, visible = true),
                Bitrate to Column(label = "Bitrate", rightAligned = true, visible = true),
                VBR to Column(label = "VBR", visible = true),
                Type to Column(label = "Type", visible = true),
            )
        )
    }
}