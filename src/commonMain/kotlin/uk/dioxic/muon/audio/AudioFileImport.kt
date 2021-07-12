package uk.dioxic.muon.audio

import kotlinx.serialization.Serializable
import uk.dioxic.muon.audio.AudioImportFieldKey.*
import uk.dioxic.muon.coalesce
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

    fun merge(other: AudioFileImport, ignoreText: String, ignoreLocation: Boolean = true) = AudioFileImport(
        id = id,
        originalTags = originalTags,
        standardizedTags = Tags(
            album = coalesce(ignoreText, other.standardizedTags.album, standardizedTags.album),
            artist = coalesce(ignoreText, other.standardizedTags.artist, standardizedTags.artist),
            title = coalesce(ignoreText, other.standardizedTags.title, standardizedTags.title),
            genre = coalesce(ignoreText, other.standardizedTags.genre, standardizedTags.genre),
            comment = coalesce(ignoreText, other.standardizedTags.comment, standardizedTags.comment),
            year = coalesce(ignoreText, other.standardizedTags.year, standardizedTags.year),
            lyricist = coalesce(ignoreText, other.standardizedTags.lyricist, standardizedTags.lyricist),
        ),
        originalLocation = originalLocation,
        standardizedLocation = if (ignoreLocation) standardizedLocation else Location(
            path = coalesce(ignoreText, other.standardizedLocation.path, standardizedLocation.path),
            filename = coalesce(ignoreText, other.standardizedLocation.filename, standardizedLocation.filename),
        ),
        header = header
    )

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
        val BLANK = build("")

        fun build(defaultText: String) =
            AudioFileImport(
                id = defaultText,
                originalTags = Tags(defaultText),
                standardizedTags = Tags(defaultText),
                originalLocation = Location(),
                standardizedLocation = Location(),
                header = Header()
            )

        fun comparator(a: AudioFileImport, b: AudioFileImport, orderBy: AudioImportFieldKey) =
            when (orderBy) {
                VBR -> a.header.vbr.compareTo(b.header.vbr)
                Length -> a.header.length.compareTo(b.header.length)
                Bitrate -> a.header.bitrate.compareTo(b.header.bitrate)
                else -> a.get(orderBy).compareTo(b.get(orderBy))
            }
    }
}
