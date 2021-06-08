package uk.dioxic.muon.audio

import kotlinx.serialization.Serializable
import uk.dioxic.muon.audio.AudioImportFieldKey.*
import uk.dioxic.muon.toTimeString

@Serializable
enum class AudioImportFieldKey {
    OriginalArtist,
    OriginalTitle,
    OriginalGenre,
    OriginalComment,
    OriginalPath,
    OriginalFilename,
    OriginalLyricist,
    OriginalYear,
    OriginalAlbum,
    StandardizedArtist,
    StandardizedTitle,
    StandardizedGenre,
    StandardizedComment,
    StandardizedPath,
    StandardizedFilename,
    StandardizedLyricist,
    StandardizedYear,
    StandardizedAlbum,
    Bitrate,
    VBR,
    Type,
    Length,
}

@Serializable
data class AudioFileImport(
    val id: String,
    val originalTags: Tags,
    val standardizedTags: Tags,
    val originalLocation: Location,
    val standardizedLocation: Location,
    val header: Header,
) {

    fun get(field: AudioImportFieldKey): String =
        when (field) {
            OriginalArtist -> originalTags.artist
            OriginalTitle -> originalTags.title
            OriginalGenre -> originalTags.genre
            OriginalComment -> originalTags.comment
            OriginalLyricist -> originalTags.lyricist
            OriginalYear -> originalTags.year
            OriginalAlbum -> originalTags.album
            OriginalFilename -> originalLocation.filename
            OriginalPath -> originalLocation.path
            StandardizedArtist -> standardizedTags.artist
            StandardizedTitle -> standardizedTags.title
            StandardizedGenre -> standardizedTags.genre
            StandardizedComment -> standardizedTags.comment
            StandardizedLyricist -> standardizedTags.lyricist
            StandardizedYear -> standardizedTags.year
            StandardizedAlbum -> standardizedTags.album
            StandardizedFilename -> standardizedLocation.filename
            StandardizedPath -> standardizedLocation.path
            VBR -> header.vbr.toString()
            Type -> header.fileType
            Length -> header.length.toTimeString()
            Bitrate -> header.bitrate.toString()
        }

    fun matches(text: String) =
        originalTags.artist.contains(text, ignoreCase = true)
                || originalTags.title.contains(text, ignoreCase = true)
                || originalTags.album.contains(text, ignoreCase = true)
                || originalTags.comment.contains(text, ignoreCase = true)
                || originalLocation.filename.contains(text, ignoreCase = true)
                || standardizedTags.artist.contains(text, ignoreCase = true)
                || standardizedTags.title.contains(text, ignoreCase = true)
                || standardizedTags.album.contains(text, ignoreCase = true)
                || standardizedTags.comment.contains(text, ignoreCase = true)
                || standardizedLocation.filename.contains(text, ignoreCase = true)

    companion object {
        const val path = "/import"
        fun comparator(a: AudioFileImport, b: AudioFileImport, orderBy: AudioImportFieldKey) =
            when (orderBy) {
                VBR -> a.header.vbr.compareTo(b.header.vbr)
                Length -> a.header.length.compareTo(b.header.length)
                Bitrate -> a.header.bitrate.compareTo(b.header.bitrate)
                else -> a.get(orderBy).compareTo(b.get(orderBy))
            }
    }
}
