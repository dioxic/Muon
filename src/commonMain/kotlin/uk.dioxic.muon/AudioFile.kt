package uk.dioxic.muon

import kotlinx.serialization.Serializable
import uk.dioxic.muon.AudioFileField.*

enum class AudioFileField { Artist, Title, Genre, Comment, Bitrate, VBR, Type, OriginalFilename, NewFilename, Length, Lyricist, Year, Album }

@Serializable
data class AudioFile(
    val path: String,
    val originalFilename: String,
    val newFilename: String,
    val artist: String,
    val title: String,
    val genre: String,
    val comment: String,
    val length: Int,
    val bitrate: Int,
    val year: String,
    val lyricist: String,
    val vbr: Boolean,
    val fileType: String,
    val album: String
) {
    fun get(field: AudioFileField): String =
        when (field) {
            Artist -> artist
            Title -> title
            Genre -> genre
            Comment -> comment
            Length -> length.toTimeString()
            Bitrate -> bitrate.toString()
            VBR -> vbr.toString()
            Type -> fileType
            OriginalFilename -> originalFilename
            NewFilename -> newFilename
            Lyricist -> lyricist
            Year -> year
            Album -> album
        }

    companion object {
        const val path = "/music"
        fun comparator(a: AudioFile, b: AudioFile, orderBy: AudioFileField) =
            when (orderBy) {
                Artist -> a.artist.compareTo(b.artist)
                Title -> a.title.compareTo(b.title)
                Genre -> a.genre.compareTo(b.genre)
                Comment -> a.comment.compareTo(b.comment)
                Bitrate -> a.bitrate.compareTo(b.bitrate)
                VBR -> a.vbr.compareTo(b.vbr)
                Type -> a.fileType.compareTo(b.fileType)
                OriginalFilename -> a.originalFilename.compareTo(b.originalFilename)
                NewFilename -> a.newFilename.compareTo(b.newFilename)
                Length -> a.length.compareTo(b.length)
                Lyricist -> a.lyricist.compareTo(b.lyricist)
                Year -> a.year.compareTo(b.year)
                Album -> a.album.compareTo(b.album)
            }
    }
}