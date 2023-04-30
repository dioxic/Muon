package uk.dioxic.muon.model

import kotlinx.serialization.Serializable

@Serializable
data class Track(
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
    val bpm: Int? = null,
    val key: String? = null,
    val rating: Int? = null,
    val color: RbColor? = null,
    val tags: List<String>? = null,
    val duplicates: List<Track>? = null,
) : IdType

typealias Tracks = List<Track> //Array<out Track>