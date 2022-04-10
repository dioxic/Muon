package uk.dioxic.muon.service

import org.apache.logging.log4j.LogManager
import uk.dioxic.muon.import.isAudioFile
import uk.dioxic.muon.import.toTrack
import uk.dioxic.muon.import.updateTags
import uk.dioxic.muon.model.Track
import uk.dioxic.muon.repository.SettingsRepository
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.*

class ImportService(private val settingsRepository: SettingsRepository) {

    private val logger = LogManager.getLogger()

    fun getTracks(): List<Track> {
        val dir = settingsRepository.get().importPath.let {
            requireNotNull(it) { "Import path not set!" }
            File(it)
        }

        require(dir.isDirectory) { "${dir.absolutePath} is not a directory!" }

        logger.info("Reading import files from ${dir.absolutePath}...")

        return dir.walk()
            .filter { it.isAudioFile }
            .map(this::toTrack)
            .toList()
    }

    fun getTrack(f: File): Track {
        require(f.isAudioFile) { "${f.name} is not an audio file!" }

        return toTrack(f)
    }

    fun importTrack(track: Track) {
        logger.info("importing track ${track.path}")

        val settings = settingsRepository.get()
        val file = File(track.path)

        require(file.isFile) { "${track.path} is not a file!" }

        val originalPath = file.toPath()

        requireNotNull(settings.importPath) { "import path is not set!" }

        val importDir = Path(settings.importPath)
        val newPath = importDir.resolve(originalPath.fileName)

        Files.move(originalPath, newPath)
    }

    fun deleteTrack(track: Track) {
        logger.info("deleting track ${track.path}")

        val settings = settingsRepository.get()
        val file = File(track.path)

        require(file.isFile) { "${track.path} is not a file!" }

        if (settings.softDelete) {
            requireNotNull(settings.deletePath) { "delete path is not set!" }
            val deleteDir = Path(settings.deletePath)
            if (!deleteDir.exists()) {
                deleteDir.createDirectories()
            }
            val originalPath = file.toPath()
            val newPath = getNonExistingPath(deleteDir.resolve(originalPath.fileName))

            Files.move(originalPath, newPath)
        } else {
            file.delete()
        }
    }

    /**
     * Check if the file exists already. If it does, find a new filename that doesn't exist.
     *
     * **Example:**
     *
     *     existingfile.txt -> existingfile (1).txt
     *
     */
    private tailrec fun getNonExistingPath(p: Path, counter: Int = 1): Path =
        if (!p.exists()) {
            p
        } else {
            val dir = p.parent
            getNonExistingPath(dir.resolve("${p.nameWithoutExtension} ($counter).${p.extension}"), counter + 1)
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