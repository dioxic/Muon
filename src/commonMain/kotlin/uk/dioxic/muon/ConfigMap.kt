package uk.dioxic.muon

import kotlinx.serialization.Serializable
import uk.dioxic.muon.config.AudioImportConfig
import uk.dioxic.muon.config.LibraryConfig

//enum class ConfigKey { AudioImport, Library }

//@Serializable
//data class ConfigMap2(val config: Map<ConfigKey, Config>) : Map<ConfigKey, Config> by config {
//
//    fun copy(key: ConfigKey, value: Config) =
//        ConfigMap2(config.mapValues { (k, v) -> if (key == k && value.key() == k) value else v })
//
//    fun getLibraryConfig() = getValue(Library) as LibraryConfig
//    fun getAudioImportConfig() = getValue(AudioImport) as AudioImportConfig
//
//    companion object {
//        const val path = "/config"
//
//        val Default = ConfigMap2(
//            mapOf(
//                Library to LibraryConfig.Default,
//                AudioImport to AudioImportConfig.Default
//            )
//        )
//    }
//}

@Serializable
data class ConfigMap(
    val importConfig: AudioImportConfig,
    val libraryConfig: LibraryConfig
) {
    companion object {
        const val path = "/config"

        val Default = ConfigMap(
            AudioImportConfig.Default,
            LibraryConfig.Default,
        )
    }
}