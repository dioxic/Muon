package uk.dioxic.muon.service

import org.apache.logging.log4j.kotlin.logger
import uk.dioxic.muon.exceptions.IdNotFoundException
import uk.dioxic.muon.import.removeIllegalFileCharacters
import uk.dioxic.muon.import.updateTags
import uk.dioxic.muon.model.Track
import uk.dioxic.muon.repository.ImportRepository
import uk.dioxic.muon.repository.RekordboxRepository
import uk.dioxic.muon.repository.SettingsRepository
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.*

class TrackService(
    private val settingsRepository: SettingsRepository,
    private val rekordboxRepository: RekordboxRepository,
    private val importRepository: ImportRepository,
) {

    private val logger = logger()

    fun getImportTracks() = importRepository.getTracks()

    fun importTrack(id: String) {
        val track = importRepository.getTrackById(id)
        require(track != null) { "track should be present by ID!" }
        logger.debug("importing track ${track.path}")

        val importDir = settingsRepository.get().importDir
        requireNotNull(importDir) { "import directory is not set!" }

        val file = File(track.path)
        require(file.isFile) { "$track is not a file!" }

        val importPath = Path(importDir)
        val newPath = importPath.resolve(track.targetFilename)

        importPath.createDirectories()

        Files.move(file.toPath(), newPath)
    }

    fun deleteTrack(id: String) {
        val prevPath = getPreviousPath(id) ?: throw IdNotFoundException(id)
        logger.debug("deleting track $prevPath")

        val settings = settingsRepository.get()
        val file = File(prevPath)

        require(file.isFile) { "$prevPath is not a file!" }

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
        val prevPath = getPreviousPath(track.id) ?: throw IdNotFoundException(track.id)
        logger.debug("updating tags for $prevPath")

        val file = File(prevPath)

        file.updateTags(track)

        // rename file
        if (file.name != track.targetFilename) {
            logger.debug("renaming filename for $prevPath")
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

    fun getTrackById(id: String): Track? =
        importRepository.getTrackById(id) ?: rekordboxRepository.getTrackById(id)

    private fun getPreviousPath(id: String) =
        importRepository.getPathById(id) ?: rekordboxRepository.getPathById(id)

    private fun Track.getPreviousPath() = getPreviousPath(id)

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
