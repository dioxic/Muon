package uk.dioxic.muon.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import uk.dioxic.muon.ConfigMap
import uk.dioxic.muon.ConfigKey

@Serializable
@SerialName("library")
data class LibraryConfig(
    val importPath: String,
    val libraryPath: String
): Config {
    companion object {
        val path = "${ConfigMap.path}/${ConfigKey.Library.name}"
        val Default = LibraryConfig(
            importPath = "J:\\import\\complete",
            libraryPath = "J:\\music\\Drum & Bass"
        )
    }

    override fun key(): ConfigKey = ConfigKey.Library
}