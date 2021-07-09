package uk.dioxic.muon.audio

import kotlinx.serialization.Serializable

@Serializable
data class Tags(
    val album: String = "",
    val artist: String = "",
    val title: String = "",
    val genre: String = "",
    val comment: String = "",
    val year: String = "",
    val lyricist: String = "",
) {
    constructor(defaultText: String): this(
        album = defaultText,
        artist = defaultText,
        title = defaultText,
        genre = defaultText,
        comment = defaultText,
        year = defaultText,
        lyricist = defaultText,
    )
}