package uk.dioxic.muon.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("library")
data class LibraryConfig(
    val importPath: String,
    val libraryPath: String
): Config {
    companion object {
        const val path = "/library"
        val Default = LibraryConfig(
            importPath = "J:\\import\\complete",
            libraryPath = "J:\\music\\Drum & Bass"
        )
    }

}