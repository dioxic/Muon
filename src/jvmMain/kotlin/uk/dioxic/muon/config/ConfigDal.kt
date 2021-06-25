package uk.dioxic.muon.config

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import uk.dioxic.muon.ConfigMap
import uk.dioxic.muon.ConfigKey
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.*

@ExperimentalPathApi
class ConfigDal(configDir: String) {
    private val log: Logger = LogManager.getLogger()
    private val configFile = Paths.get(configDir, "config.json")
    private var configMap: ConfigMap
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
        val configPath = Paths.get(configDir)
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
        configMap = if (configFile.exists()) {
            try {
                json.decodeFromString(Files.readString(configFile))
            } catch (e: Exception) {
                log.error(e.message)
                default()
            }
        } else {
            default()
        }
    }

    private fun default(): ConfigMap {
        log.info("Saving default config")
        save(ConfigMap.Default)
        return ConfigMap.Default
    }

    fun getLibraryConfig() = configMap.getLibraryConfig()
    fun getAudioImportConfig() = configMap.getAudioImportConfig()

    fun getAll() = configMap

    operator fun get(key: ConfigKey) = configMap.getValue(key)

    operator fun set(key: ConfigKey, value: Config) {
        configMap = configMap.copy(key, value)
//        configMap = when (key) {
//            ConfigKey.AudioImport -> {
//                require(value is AudioImportConfig) { "Invalid audio import config" }
//                configMap.copy(audioImportConfig = value)
//            }
//            ConfigKey.Library -> {
//                require(value is LibraryConfig) { "Invalid library config" }
//                configMap.copy(libraryConfig = value)
//            }
//        }
        save(configMap)
    }

    private fun save(configMap: ConfigMap) {
        Files.writeString(configFile, json.encodeToString(configMap))
        this.configMap = configMap
    }
}

