package uk.dioxic.muon.repository

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import uk.dioxic.muon.config.Settings
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists

class SettingsRepository(path: Path) {

    private val log: Logger = LogManager.getLogger()
    private val settingsFile = path.resolve("settings.json")
    private var settings: Settings = loadSettings()

    fun get() = settings

    fun save(settings: Settings) {
        log.debug("Saving settings")
        Files.writeString(settingsFile, Json.encodeToString(settings))
        this.settings = settings
    }

    private fun loadSettings(): Settings {
        var settings = Settings.DEFAULT
        if (settingsFile.exists()) {
            try {
                settings = Json.decodeFromString(Files.readString(settingsFile))
            } catch (e: Exception) {
                log.error("Settings file corrupt - saving default")
                save(Settings.DEFAULT)
            }
        } else {
            log.info("Settings file not present - saving default")
            save(Settings.DEFAULT)
        }
        return settings
    }
}