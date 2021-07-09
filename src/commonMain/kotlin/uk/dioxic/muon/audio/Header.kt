package uk.dioxic.muon.audio

import kotlinx.serialization.Serializable

@Serializable
data class Header(
    val length: Int = 0,
    val bitrate: Int = 0,
    val vbr: Boolean = false,
    val fileType: String = "",
)