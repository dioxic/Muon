package uk.dioxic.muon.audio

import kotlinx.serialization.Serializable
import uk.dioxic.muon.coalesce

@Serializable
data class AudioFile(
    val id: String,
    val location: Location,
    val tags: Tags,
    val header: Header
) {

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
            extension = coalesce(ignoreText, other.location.extension, location.extension),
        ),
        header = header
    )

    fun normalizedFilename(): String {
        if (tags.artist.isNotBlank() && tags.title.isNotBlank()) {
            val normalizedFilename = "${tags.artist.trim()} - ${tags.title.trim()}.${location.extension}"
            if (normalizedFilename != location.filename) {
                return normalizedFilename
            }
        }
        return this.location.filename
    }

    companion object {
        val BLANK = build("")

        fun build(defaultText: String) =
            AudioFile(
                id = defaultText,
                tags = Tags(defaultText),
                location = Location(defaultText),
                header = Header()
            )
    }
}