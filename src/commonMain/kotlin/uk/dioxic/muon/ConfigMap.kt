package uk.dioxic.muon

import kotlinx.serialization.Serializable
import uk.dioxic.muon.ConfigKey.AudioImport
import uk.dioxic.muon.ConfigKey.Library
import uk.dioxic.muon.config.AudioImportConfig
import uk.dioxic.muon.config.Config
import uk.dioxic.muon.config.LibraryConfig

enum class ConfigKey { AudioImport, Library }

@Serializable
data class ConfigMap(val config: Map<ConfigKey, Config>) : Map<ConfigKey, Config> by config {

    fun copy(key: ConfigKey, value: Config) =
        ConfigMap(config.mapValues { (k, v) -> if (key == k && value.key() == k) value else v })

    fun getLibraryConfig() = getValue(Library) as LibraryConfig
    fun getAudioImportConfig() = getValue(AudioImport) as AudioImportConfig

    companion object {
        const val path = "/config"

        val Default = ConfigMap(
            mapOf(
                Library to LibraryConfig.Default,
                AudioImport to AudioImportConfig.Default
            )
        )
    }
}

@Serializable
data class ConfigRoot(
    val importConfig: AudioImportConfig,
    val libraryConfig: LibraryConfig
) {
    companion object {
        const val path = "/config"

        val Default = ConfigRoot(
            AudioImportConfig.Default,
            LibraryConfig.Default,
        )
    }
}