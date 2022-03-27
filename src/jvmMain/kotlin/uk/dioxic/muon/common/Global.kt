package uk.dioxic.muon.common

import uk.dioxic.muon.config.Settings
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.*

object Global {
    val homePath: Path = initHomePath()
//    var settings: Settings = loadSettings()
//        set(value) {
//            saveSettings(value)
//            field = value
//        }

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

