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
    constructor(defaultText: String) : this(
        album = defaultText,
        artist = defaultText,
        title = defaultText,
        genre = defaultText,
        comment = defaultText,
        year = defaultText,
        lyricist = defaultText,
    )
}

fun findCommonFields(defaultText: String = "", tags: List<Tags>): Tags {
    var common = tags.first()

    tags.forEach {
        common = Tags(
            album = if (common.album == it.album) common.album else defaultText,
            artist = if (common.artist == it.artist) common.artist else defaultText,
            title = if (common.title == it.title) common.title else defaultText,
            genre = if (common.genre == it.genre) common.genre else defaultText,
            comment = if (common.comment == it.comment) common.comment else defaultText,
            year = if (common.year == it.year) common.year else defaultText,
            lyricist = if (common.lyricist == it.lyricist) common.lyricist else defaultText,
        )
    }
    return common
}