package uk.dioxic.muon

import kotlinx.serialization.Serializable
import uk.dioxic.muon.AudioFileFieldKey.*

enum class AudioFileFieldKey { Artist, Title, Genre, Comment, Bitrate, VBR, Type, Path, Filename, Length, Lyricist, Year, Album }

@Serializable
data class AudioFile(
    val location: Location,
    val tags: Tags,
    val header: Header
) {
    fun get(field: AudioFileFieldKey): String =
        when (field) {
            Artist -> tags.artist
            Title -> tags.title
            Genre -> tags.genre
            Comment -> tags.comment
            Length -> header.length.toTimeString()
            Bitrate -> header.bitrate.toString()
            VBR -> header.vbr.toString()
            Type -> header.fileType
            Filename -> location.filename
            Path -> location.path
            Lyricist -> tags.lyricist
            Year -> tags.year
            Album -> tags.album
        }

    fun matches(text: String) =
        tags.artist.contains(text, ignoreCase = true)
                || tags.title.contains(text, ignoreCase = true)
                || tags.album.contains(text, ignoreCase = true)
                || tags.comment.contains(text, ignoreCase = true)
                || location.filename.contains(text, ignoreCase = true)

    companion object {
        const val path = "/music"
        fun comparator(a: AudioFile, b: AudioFile, orderBy: AudioFileFieldKey) =
            when (orderBy) {
                Bitrate -> a.header.bitrate.compareTo(b.header.bitrate)
                VBR -> a.header.vbr.compareTo(b.header.vbr)
                Length -> a.header.length.compareTo(b.header.length)
                else -> a.get(orderBy).compareTo(b.get(orderBy))
            }
    }
}