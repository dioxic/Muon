package uk.dioxic.muon.repository

import uk.dioxic.muon.model.ConfigMap
import uk.dioxic.muon.config.AudioImportConfig
import uk.dioxic.muon.config.Config
import uk.dioxic.muon.config.LibraryConfig

interface ConfigRepository {
    fun getFullConfig() : ConfigMap

    fun getLibraryConfig(): LibraryConfig

    fun getImportConfig(): AudioImportConfig

    fun save(config: Config)

    fun save(configMap: ConfigMap)
}