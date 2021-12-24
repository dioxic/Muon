package uk.dioxic.muon.config

import kotlinx.serialization.Serializable
import uk.dioxic.muon.audio.AudioFile
import uk.dioxic.muon.audio.AudioFile.Keys.*
import uk.dioxic.muon.model.Column

@Serializable
data class AudioImportConfig (
    val columns: Map<AudioFile.Keys, Column>
): Config {
    companion object {
        const val path = "import"
        val Default = AudioImportConfig(
            linkedMapOf(
                Artist to Column(label = "Artist", visible = true),
                Title to Column(label = "Title", visible = false),
                Genre to Column(label = "Genre", visible = true),
                Comment to Column(label = "Comment", visible = true),
                Lyricist to Column(label = "Lyricist", visible = true),
                Year to Column(label = "Year", rightAligned = true, visible = true),
                Album to Column(label = "Album", visible = true),
                Filename to Column(label = "Filename", visible = true),
                Length to Column(label = "Length", rightAligned = true, visible = true),
                Bitrate to Column(label = "Bitrate", rightAligned = true, visible = true),
                VBR to Column(label = "VBR", visible = true),
                Type to Column(label = "Type", visible = true),
            )
        )
    }

}