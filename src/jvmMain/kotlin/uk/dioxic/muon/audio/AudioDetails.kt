package uk.dioxic.muon.audio

import kotlinx.serialization.Serializable

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
    }
}