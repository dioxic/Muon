package uk.dioxic.muon.audio

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val path: String = "",
    val filename: String = "",
    val extension: String = "",
) {
    constructor(defaultText: String) : this(
        path = defaultText,
        filename = defaultText,
        extension = defaultText
    )
}

fun findCommonFields(defaultText: String = "", locations: List<Location>): Location {
    var common = locations.first()

    locations.forEach {
        common = Location(
            path = if (common.path == it.path) common.path else defaultText,
            filename = if (common.filename == it.filename) common.filename else defaultText,
            extension = if (common.extension == it.extension) common.extension else defaultText,
        )
        println("common: $common")
    }
    return common
}