package uk.dioxic.muon.service

import org.apache.logging.log4j.LogManager
import uk.dioxic.muon.import.isAudioFile
import uk.dioxic.muon.import.toTrack
import uk.dioxic.muon.import.updateTags
import uk.dioxic.muon.model.Track
import uk.dioxic.muon.repository.SettingsRepository
import java.io.File
import java.nio.file.Files
import kotlin.io.path.extension
import kotlin.io.path.pathString

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

    fun updateTrack(f: File, track: Track): Track {
        logger.info("updating tags for ${track.path}")

        f.updateTags(track)

        // rename file
        if (f.nameWithoutExtension != track.filename) {
            logger.info("renaming filename for ${track.path}")
            val originalPath = f.toPath()
            val newPath = originalPath.parent.resolve("${track.filename}.${originalPath.extension}")
            Files.move(originalPath, newPath)
            return track.copy(path = newPath.pathString)
        }
        return track
    }

    private fun toTrack(f: File) =
        try {
            f.toTrack()
        } catch (e: Throwable) {
            logger.error("error on ${f.name}")
            throw e
        }


}