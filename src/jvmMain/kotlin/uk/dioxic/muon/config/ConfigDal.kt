package uk.dioxic.muon.config

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.logging.log4j.LogBuilder
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import uk.dioxic.muon.AudioImportConfig
import uk.dioxic.muon.Config
import uk.dioxic.muon.ConfigKey
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.*

@ExperimentalPathApi
class ConfigDal(configDir: String) {
    private val log: Logger = LogManager.getLogger()
    private val configFile = Paths.get(configDir, "config.json")
    private var config: Config
    private val json = Json {
        prettyPrint = true
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
        config = if (configFile.exists()) {
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

    private fun default(): Config {
        log.info("Saving default config")
        save(Config.Default)
        return Config.Default
    }

    fun get(key: ConfigKey) = config[key]

    fun get() = config

    fun set(key: ConfigKey, value: Any) {
        when (key) {
            ConfigKey.AudioImport -> {
                require(value is AudioImportConfig) { "Invalid audio import config" }
                config = config.copy(audioImportConfig = value)
            }
        }
        save(config)
    }

    private fun save(config: Config) {
        Files.writeString(configFile, json.encodeToString(config))
        this.config = config
    }
}

