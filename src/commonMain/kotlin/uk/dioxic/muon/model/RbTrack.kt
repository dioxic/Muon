package uk.dioxic.muon.model

import kotlinx.serialization.Serializable

@Serializable
data class RbTrack(
    override val id: String,
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
    val year: String,
    val fileSize: Int,
    val type: FileType,
    val bpm: Int,
    val key: String,
    val rating: Int,
    val color: Color?,
    val tags: List<String>,
): IdType

enum class Color {
    PINK,
    RED,
    ORANGE,
    YELLOW,
    GREEN,
    AQUA,
    BLUE,
    PURPLE,
}