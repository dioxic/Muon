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

private val log: Logger = LogManager.getLogger()

private val settingsFile = Global.homePath.resolve("settings.json")

fun saveSettings(settings: Settings) {
    log.debug("Saving settings")
    Files.writeString(settingsFile, Json.encodeToString(settings))
}

fun loadSettings(): Settings {
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