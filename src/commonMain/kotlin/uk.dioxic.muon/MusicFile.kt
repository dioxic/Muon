package uk.dioxic.muon

import kotlinx.serialization.Serializable
import uk.dioxic.muon.MusicFileField.*

enum class MusicFileField { Artist, Title, Genre, Comment, Bitrate, VBR, Type, Filename, Length }

@Serializable
data class MusicFile(
    val path: String,
    val filename: String,
    val artist: String,
    val title: String,
    val genre: String,
    val comment: String,
    val length: Int,
    val bitrate: Int,
    val vbr: Boolean,
    val fileType: String
) {
    fun get(field: MusicFileField): String =
        when (field) {
            Artist -> artist
            Title -> title
            Genre -> genre
            Comment -> comment
            Length -> length.toTimeString()
            Bitrate -> bitrate.toString()
            VBR -> vbr.toString()
            Type -> fileType
            Filename -> filename
        }

    companion object {
        const val path = "/music"
        fun comparator(a: MusicFile, b: MusicFile, orderBy: MusicFileField) =
            when (orderBy) {
                Artist -> a.artist.compareTo(b.artist)
                Title -> a.title.compareTo(b.title)
                Genre -> a.genre.compareTo(b.genre)
                Comment -> a.comment.compareTo(b.comment)
                Bitrate -> a.bitrate.compareTo(b.bitrate)
                VBR -> a.vbr.compareTo(b.vbr)
                Type -> a.fileType.compareTo(b.fileType)
                Filename -> a.filename.compareTo(b.filename)
                Length -> a.length.compareTo(b.length)
            }
    }
}