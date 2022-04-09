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
            .map(this::toTrack)
            .toList()
    }

    fun getTrack(f: File): Track {
        require(f.isAudioFile) { "${f.name} is not an audio file!" }

        return toTrack(f)
    }

    fun updateTrack(f: File, track: Track) {
        println("updating track...")
        throw IllegalStateException("blah blah blah")
    }

    private fun toTrack(f: File) =
        try {
            f.toTrack()
        } catch (e: Throwable) {
            logger.error("error on ${f.name}")
            throw e
        }



}