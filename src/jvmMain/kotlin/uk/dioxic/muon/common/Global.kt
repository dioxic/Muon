package uk.dioxic.muon.common

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import uk.dioxic.muon.config.Settings
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.*

object Global {
    private val log: Logger = LogManager.getLogger()
    val homePath: Path = initHomePath()
    private val settingsFile = homePath.resolve("settings.json")
    var settings: Settings = loadSettings()
        set(value) {
            saveSettings(value)
            field = value
        }

    private fun saveSettings(settings: Settings) {
        log.debug("Saving settings")
        Files.writeString(settingsFile, Json.encodeToString(settings))
    }

    private fun loadSettings(): Settings {
        var settings = Settings.DEFAULT
        if (settingsFile.exists()) {
            try {
                settings = Json.decodeFromString(Files.readString(settingsFile))
            } catch (e: Exception) {
                log.error("Settings file corrupt - saving default")
                saveSettings(Settings.DEFAULT)
            }
        } else {
            log.info("Settings file not present - saving default")
            saveSettings(Settings.DEFAULT)
        }
        return settings
    }

    private fun initHomePath(): Path {
        val configPath = Paths.get("${System.getenv("HOMEPATH")}/.muon")

        // create the muon directory if it doesn't exist
        if (!configPath.exists()) {
            if (configPath.parent.isDirectory()) {
                configPath.createDirectory()
            } else {
                throw IllegalStateException("cannot create .muon config directory")
            }
        }

        // check the muon directory is valid
        require(configPath.isDirectory()) { "config dir must be a directory" }
        require(configPath.isReadable()) { "config dir must be readable" }
        require(configPath.isWritable()) { "config dir must be writable" }

        return configPath
    }
}

