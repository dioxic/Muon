package uk.dioxic.muon.config

import kotlinx.serialization.Serializable
import uk.dioxic.muon.Config
import uk.dioxic.muon.ConfigKey

@Serializable
data class LibraryConfig(
    val importPath: String,
    val libraryPath: String
) {
    companion object {
        val path = "${Config.path}/${ConfigKey.Library.name}"
        val Default = LibraryConfig(
            importPath = "J:\\import\\complete",
            libraryPath = "J:\\music\\Drum & Bass"
        )
    }
}