package uk.dioxic.muon.service

import org.apache.logging.log4j.LogManager
import uk.dioxic.muon.import.isAudioFile
import uk.dioxic.muon.import.removeIllegalFileCharacters
import uk.dioxic.muon.import.toTrack
import uk.dioxic.muon.import.updateTags
import uk.dioxic.muon.model.Track
import uk.dioxic.muon.repository.SettingsRepository
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.*

class TrackService(
    private val settingsRepository: SettingsRepository,
) {

    private val logger = LogManager.getLogger()

    fun getImportTracks(): List<Track> {
        val dirs = settingsRepository.get().downloadDirs
        require(dirs.isNotEmpty()) { "download directory not set!" }

        return dirs.map { File(it) }
            .flatMap(::getTracks)
    }

    private fun getTracks(dir: File): List<Track> {
        require(dir.isDirectory) { "${dir.absolutePath} is not a directory!" }

        logger.debug("Reading files from ${dir.absolutePath}...")

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
        logger.debug("importing track ${track.path}")

        val importDir = settingsRepository.get().importDir
        requireNotNull(importDir) { "import directory is not set!" }

        val file = File(track.path)
        require(file.isFile) { "${track.path} is not a file!" }

        val importPath = Path(importDir)
        val newPath = importPath.resolve(track.targetFilename)

        importPath.createDirectories()

        Files.move(file.toPath(), newPath)
    }

    fun deleteTrack(track: Track) {
        logger.debug("deleting track ${track.path}")

        val settings = settingsRepository.get()
        val file = File(track.path)

        require(file.isFile) { "${track.path} is not a file!" }

        if (settings.softDelete) {
            requireNotNull(settings.deleteDir) { "delete directory is not set!" }
            val deleteDir = Path(settings.deleteDir)
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

    fun updateTrack(track: Track): Track {
        logger.debug("updating tags for ${track.path}")

        val file = File(track.path)

        file.updateTags(track)

        // rename file
        if (file.name != track.targetFilename) {
            logger.debug("renaming filename for ${track.path}")
            val originalPath = file.toPath()
            val newPath = originalPath.parent.resolve(track.targetFilename)
            Files.move(originalPath, newPath)
            return track.copy(
                path = newPath.pathString,
                filename = track.targetFilename,
            )
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

    private val Track.targetFilename: String
        get() = if (settingsRepository.get().standardiseFilenames) {
            "$artist - ${title}.${Paths.get(path).extension}"
        } else {
            "$filename.${Paths.get(path).extension}"
        }.removeIllegalFileCharacters()

}
