package uk.dioxic.muon.config

import kotlinx.serialization.Serializable
import uk.dioxic.muon.model.Library

@Serializable
data class LibraryConfig(
    val libraries: List<Library>,
    val source: String?,
) : Config {
    companion object {
        const val path = "library"
        val Default = LibraryConfig(
            libraries = listOf(),
            source = null
        )
    }
}