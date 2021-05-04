package uk.dioxic.muon

import kotlinx.serialization.Serializable

@Serializable
data class Tags(
    val album: String,
    val artist: String,
    val title: String,
    val genre: String,
    val comment: String,
    val year: String,
    val lyricist: String,
)