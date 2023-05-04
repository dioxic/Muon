package uk.dioxic.muon.repository

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.logging.log4j.kotlin.logger
import uk.dioxic.muon.common.validate
import uk.dioxic.muon.config.Settings
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists

typealias SettingsListener = (Settings) -> Unit

class SettingsRepository(path: Path) {

    private val logger = logger()
    private val settingsFile = path.resolve("settings.json")
    private val listeners = mutableListOf<SettingsListener>()
    private var settings: Settings = loadSettings()

    fun get() = settings

    fun save(settings: Settings): Settings {
        logger.debug("Saving settings")
        settings.validate()
        Files.writeString(settingsFile, Json.encodeToString(settings))
        this.settings = settings
        notifyListeners()
        return settings
    }

    fun addListener(listener: SettingsListener) =
        listeners.add(listener)

    private fun notifyListeners() =
        listeners.forEach { it(settings) }

    private fun loadSettings(): Settings {
        var settings = Settings.DEFAULT
        if (settingsFile.exists()) {
            try {
                settings = Json.decodeFromString(Files.readString(settingsFile))
            } catch (e: Exception) {
                logger.error("Settings file corrupt - saving default")
                save(Settings.DEFAULT)
            }
        } else {
            logger.info("Settings file not present - saving default")
            save(Settings.DEFAULT)
        }
        return settings
    }
}