package uk.dioxic.muon.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import uk.dioxic.muon.audio.AudioImportFieldKey
import uk.dioxic.muon.audio.AudioImportFieldKey.*
import uk.dioxic.muon.Column
import uk.dioxic.muon.ConfigMap
import uk.dioxic.muon.ConfigKey

@Serializable
@SerialName("import")
data class AudioImportConfig (
    val columns: Map<AudioImportFieldKey, Column>
): Config {
    companion object {
        val path = "${ConfigMap.path}/${ConfigKey.AudioImport.name}"
        val Default = AudioImportConfig(
            linkedMapOf(
                OriginalArtist to Column(
                    label = "Original Artist",
                    disablePadding = true,
                    visible = false
                ),
                StandardizedArtist to Column(label = "Artist", visible = true),
                OriginalTitle to Column(label = "Original Title", visible = false),
                StandardizedTitle to Column(label = "Title", visible = true),
                OriginalGenre to Column(label = "Original Genre", visible = false),
                StandardizedGenre to Column(label = "Genre", visible = true),
                OriginalComment to Column(label = "Original Comment", visible = false),
                StandardizedComment to Column(label = "Comment", visible = true),
                OriginalLyricist to Column(label = "Original Lyricist", visible = false),
                StandardizedLyricist to Column(label = "Lyricist", visible = true),
                OriginalYear to Column(
                    label = "Original Year",
                    rightAligned = true,
                    visible = false
                ),
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

    override fun key(): ConfigKey = ConfigKey.AudioImport
}