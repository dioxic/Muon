package uk.dioxic.muon.repository

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import uk.dioxic.muon.model.ConfigMap
import uk.dioxic.muon.config.AudioImportConfig
import uk.dioxic.muon.config.Config
import uk.dioxic.muon.config.LibraryConfig
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.*

class ConfigRepositoryImpl(configDirectory: String) : ConfigRepository {
    private val log: Logger = LogManager.getLogger()
    private val configFile = Paths.get(configDirectory, "config.json")
    private var configMap: ConfigMap = ConfigMap.Default
    private val json = Json {
        prettyPrint = true
        serializersModule = SerializersModule {
            polymorphic(Config::class) {
                subclass(LibraryConfig::class, LibraryConfig.serializer())
                subclass(AudioImportConfig::class, AudioImportConfig.serializer())
            }
        }
    }

    init {
        val configPath = Paths.get(configDirectory)
        if (!configPath.exists()) {
            if (configPath.parent.isDirectory()) {
                configPath.createDirectory()
            } else {
                throw IllegalArgumentException("cannot create .muon config directory")
            }
        }
        require(configPath.isDirectory()) { "config dir must be a directory" }
        require(configPath.isReadable()) { "config dir must be readable" }
        require(configPath.isWritable()) { "config dir must be writable" }
        if (configFile.exists()) {
            try {
                configMap = json.decodeFromString(Files.readString(configFile))
            } catch (e: Exception) {
                log.error("Config file corrupt - saving default")
                save(ConfigMap.Default)
            }
        } else {
            log.info("Config file not present - saving default")
            save(ConfigMap.Default)
        }
    }

    override fun getFullConfig(): ConfigMap = configMap

    override fun getLibraryConfig(): LibraryConfig = configMap.libraryConfig

    override fun getImportConfig(): AudioImportConfig = configMap.importConfig

    override fun save(config: Config) {
        configMap =
            when (config) {
                is LibraryConfig -> configMap.copy(libraryConfig = config)
                is AudioImportConfig -> configMap.copy(importConfig = config)
            }
        save(configMap)
    }

    override fun save(configMap: ConfigMap) {
        log.debug("Saving config")
        Files.writeString(configFile, json.encodeToString(configMap))
    }

}