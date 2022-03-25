package uk.dioxic.muon.common

import uk.dioxic.muon.config.Settings
import java.nio.file.Path

object Global {
    val homePath: Path = initHomePath()
    var settings: Settings = loadSettings()
        set(value) {
            saveSettings(value)
            field = value
        }

}

