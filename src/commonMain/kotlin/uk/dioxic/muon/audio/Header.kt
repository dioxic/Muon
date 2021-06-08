package uk.dioxic.muon.audio

import kotlinx.serialization.Serializable

@Serializable
data class Header(
    val length: Int,
    val bitrate: Int,
    val vbr: Boolean,
    val fileType: String,
)