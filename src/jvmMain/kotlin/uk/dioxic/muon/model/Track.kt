package uk.dioxic.muon.model

import java.nio.file.Path

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val genre: String,
    val album: String,
    val lyricist: String,
    val comment: String,
    val bitrate: Int,
    val path: String,
    val filename: String,
    val length: Int,
    val year: Int,
)