package uk.dioxic.muon

import kotlinx.serialization.Serializable
import uk.dioxic.muon.config.AudioImportConfig
import uk.dioxic.muon.config.LibraryConfig

@Serializable
data class ConfigMap(
    val importConfig: AudioImportConfig,
    val libraryConfig: LibraryConfig
) {
    companion object {
        val Default = ConfigMap(
            AudioImportConfig.Default,
            LibraryConfig.Default,
        )
    }
}