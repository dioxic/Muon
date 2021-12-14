package uk.dioxic.muon.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("library")
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

@Serializable
data class Library(
    val name: String,
    val path: String,
    val genre: String?,
)