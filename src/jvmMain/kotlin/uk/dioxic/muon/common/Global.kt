package uk.dioxic.muon.common

import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.*

object Global {
    val configPath: Path = initHomePath()

    private fun initHomePath(): Path {
        val homePath = System.getenv("HOMEPATH") ?: error("environment variable HOMEPATH not set!")
        val configPath = Paths.get("$homePath/.muon")

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

