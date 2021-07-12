package uk.dioxic.muon.audio

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val path: String = "",
    val filename: String = ""
) {
    constructor(defaultText: String) : this(
        path = defaultText,
        filename = defaultText,
    )
}