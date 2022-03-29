package uk.dioxic.muon.service

import org.apache.logging.log4j.LogManager
import uk.dioxic.muon.import.isAudioFile
import uk.dioxic.muon.import.toTrack
import uk.dioxic.muon.model.Track
import uk.dioxic.muon.repository.SettingsRepository
import java.io.File
import java.nio.file.Path

class ImportService(private val settingsRepository: SettingsRepository) {

    private val logger = LogManager.getLogger()

    fun getTracks(): List<Track> {
        val dir = settingsRepository.get().importPath.let {
            requireNotNull(it) { "Import path not set!" }
            File(it)
        }

        require(dir.isDirectory) { "${dir.name} is not a directory!" }

        logger.info("Reading import files from ${dir.name}...")

        return dir.walk()
            .filter { it.isAudioFile }
            .map { it.toTrack() }
            .toList()
    }

    fun updateTrack(path: Path, track: Track) {

    }

}