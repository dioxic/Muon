package uk.dioxic.muon

import kotlinx.serialization.Serializable

@Serializable
data class Header(
    val length: Int,
    val bitrate: Int,
    val vbr: Boolean,
    val fileType: String,
)