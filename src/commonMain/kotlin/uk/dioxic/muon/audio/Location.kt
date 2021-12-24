package uk.dioxic.muon.audio

import kotlinx.serialization.Serializable
import uk.dioxic.muon.fileExtension

@Serializable
data class Location(
    val path: String = "",
    val filename: String = "",
    val extension: String = filename.fileExtension() ?: "",
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
    }
    return common
}