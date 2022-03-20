package uk.dioxic.muon.audio

import kotlinx.serialization.Serializable
import uk.dioxic.muon.model.ColumnKeys
import uk.dioxic.muon.model.ColumnKeys.*
import uk.dioxic.muon.toTimeString

@Serializable
data class AudioDetails(
    val audioFile: AudioFile,
    val duplicates: List<AudioDetails>? = null,
    val score: Float? = null,
    val docId: Int? = null,
) {
    fun merge(other: AudioFile, ignoreText: String) = AudioDetails(
        audioFile = audioFile.merge(other, ignoreText),
        duplicates = duplicates,
        docId = docId,
        score = score,
    )

    fun matches(text: String) =
        audioFile.tags.artist.contains(text, ignoreCase = true)
                || audioFile.tags.title.contains(text, ignoreCase = true)
                || audioFile.tags.album.contains(text, ignoreCase = true)
                || audioFile.tags.comment.contains(text, ignoreCase = true)
                || audioFile.location.filename.contains(text, ignoreCase = true)

    operator fun get(field: ColumnKeys): String =
        when (field) {
            Artist -> audioFile.tags.artist
            Title -> audioFile.tags.title
            Genre -> audioFile.tags.genre
            Comment -> audioFile.tags.comment
            Length -> audioFile.header.length.toTimeString()
            Bitrate -> audioFile.header.bitrate.toString()
            VBR -> audioFile.header.vbr.toString()
            Type -> audioFile.header.fileType
            Filename -> audioFile.location.filename
            Path -> audioFile.location.path
            Lyricist -> audioFile.tags.lyricist
            Year -> audioFile.tags.year
            Album -> audioFile.tags.album
            Score -> score.toString()
        }

    companion object {
        val BLANK = build("")

        fun build(defaultText: String) =
            AudioDetails(
                audioFile = AudioFile(
                    id = defaultText,
                    tags = Tags(defaultText),
                    location = Location(defaultText),
                    header = Header()
                ),
                score = 0.0f
            )

        fun comparator(a: AudioDetails, b: AudioDetails, orderBy: ColumnKeys) =
            when (orderBy) {
                Bitrate -> a.audioFile.header.bitrate.compareTo(b.audioFile.header.bitrate)
                VBR -> a.audioFile.header.vbr.compareTo(b.audioFile.header.vbr)
                Length -> a.audioFile.header.length.compareTo(b.audioFile.header.length)
                else -> a[orderBy].compareTo(b[orderBy])
            }
    }
}