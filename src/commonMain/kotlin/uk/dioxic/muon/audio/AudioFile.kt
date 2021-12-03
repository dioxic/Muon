package uk.dioxic.muon.audio

import kotlinx.serialization.Serializable
import uk.dioxic.muon.audio.AudioFile.Keys.*
import uk.dioxic.muon.coalesce
import uk.dioxic.muon.toTimeString

//enum class AudioFileFieldKey { Artist, Title, Genre, Comment, Bitrate, VBR, Type, Path, Filename, Length, Lyricist, Year, Album }

@Serializable
data class AudioFile(
    val id: String,
    val location: Location,
    val tags: Tags,
    val header: Header
) {
    enum class Keys { Artist, Title, Genre, Comment, Bitrate, VBR, Type, Path, Filename, Length, Lyricist, Year, Album }

    fun get(field: Keys): String =
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

    fun merge(other: AudioFile, ignoreText: String) = AudioFile(
        id = id,
        tags = Tags(
            album = coalesce(ignoreText, other.tags.album, tags.album),
            artist = coalesce(ignoreText, other.tags.artist, tags.artist),
            title = coalesce(ignoreText, other.tags.title, tags.title),
            genre = coalesce(ignoreText, other.tags.genre, tags.genre),
            comment = coalesce(ignoreText, other.tags.comment, tags.comment),
            year = coalesce(ignoreText, other.tags.year, tags.year),
            lyricist = coalesce(ignoreText, other.tags.lyricist, tags.lyricist),
        ),
        location = Location(
            path = coalesce(ignoreText, other.location.path, location.path),
            filename = coalesce(ignoreText, other.location.filename, location.filename),
        ),
        header = header
    )

    companion object {
        val BLANK = build("")

        fun build(defaultText: String) =
            AudioFile(
                id = defaultText,
                tags = Tags(defaultText),
                location = Location(),
                header = Header()
            )

        fun comparator(a: AudioFile, b: AudioFile, orderBy: Keys) =
            when (orderBy) {
                Bitrate -> a.header.bitrate.compareTo(b.header.bitrate)
                VBR -> a.header.vbr.compareTo(b.header.vbr)
                Length -> a.header.length.compareTo(b.header.length)
                else -> a.get(orderBy).compareTo(b.get(orderBy))
            }
    }
}