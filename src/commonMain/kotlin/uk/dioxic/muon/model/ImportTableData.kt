package uk.dioxic.muon.model

import kotlinx.serialization.Serializable

@Serializable
data class ImportTableData(
    val id: String,
    val title: String = "",
    val artist: String = "",
    val lyricist: String = "",
    val genre: String = "",
    val comment: String = "",
    val bitrate: Int,
    val vbr: Boolean,
    val type: String = "",
    val path: String,
    val filename: String,
    val length: Int,
    val year: String = ""
)
