package uk.dioxic.muon

import kotlinx.serialization.Serializable
import uk.dioxic.muon.ConfigKey.AudioImport
import uk.dioxic.muon.ConfigKey.Library
import uk.dioxic.muon.config.AudioImportConfig
import uk.dioxic.muon.config.LibraryConfig

enum class ConfigKey { AudioImport, Library }

@Serializable
data class Config(
    val audioImportConfig: AudioImportConfig,
    val libraryConfig: LibraryConfig
) {

    operator fun get(key: ConfigKey): Any =
        when (key) {
            AudioImport -> audioImportConfig
            Library -> libraryConfig
        }

    companion object {
        const val path = "/config"
        val Default = Config(
            audioImportConfig = AudioImportConfig.Default,
            libraryConfig = LibraryConfig.Default
        )
    }
}

