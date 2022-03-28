package uk.dioxic.muon.audio

import kotlinx.serialization.Serializable

@Serializable
data class Header(
    val length: Int = 0,
    val bitrate: Int = 0,
    val vbr: Boolean = false,
    val fileType: String = "",
)

fun findCommonFields(headers: List<Header>): Header {
    var common = headers.first()

    headers.forEach {
        common = Header(
            length = if (common.length == it.length) common.length else 0,
            bitrate = if (common.bitrate == it.bitrate) common.bitrate else 0,
            vbr = if (common.vbr == it.vbr) common.vbr else false,
            fileType = if (common.fileType == it.fileType) common.fileType else "",
        )
    }
    return common
}