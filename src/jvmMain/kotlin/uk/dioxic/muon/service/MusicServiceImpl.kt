package uk.dioxic.muon.service

import org.apache.logging.log4j.LogManager
import org.jaudiotagger.audio.AudioFileIO
import uk.dioxic.muon.audio.AudioFile
import uk.dioxic.muon.audio.ImportError
import uk.dioxic.muon.exceptions.MusicImportException
import uk.dioxic.muon.getPath
import uk.dioxic.muon.merge
import uk.dioxic.muon.repository.MusicRepository
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.io.path.extension

class MusicServiceImpl(private val musicRepository: MusicRepository) : MusicRepository by musicRepository,
    MusicService {

    private val logger = LogManager.getLogger()

    override fun update(libraryId: String?, audioFile: AudioFile) {
        val cachedFile = musicRepository.getById(audioFile.id)
        val overwrite = false

        if (audioFile.location != cachedFile.location) {
            logger.debug("Importing file ${audioFile.location.filename}")

            require(audioFile.getPath().extension.isNotEmpty()) { "Missing file extension" }

            if (overwrite) {
                Files.move(cachedFile.getPath(), audioFile.getPath(), StandardCopyOption.REPLACE_EXISTING)
            } else {
                Files.move(cachedFile.getPath(), audioFile.getPath())
            }
        }

        if (audioFile.tags != cachedFile.tags) {
            logger.debug("Saving tags for file ${cachedFile.getPath()}")
            AudioFileIO.read(audioFile.getPath().toFile())?.also {
                it.merge(audioFile)
                it.commit()
            }
        }

        musicRepository.update(libraryId, audioFile)
    }

    override fun updateMany(libraryId: String?, audioFiles: List<AudioFile>) {
        val errors = mutableListOf<ImportError>()
        audioFiles.forEach {
            try {
                update(libraryId, it)
            } catch (e: FileAlreadyExistsException) {
                errors.add(
                    ImportError(
                        id = it.id,
                        filename = it.location.filename,
                        reason = "File already exists!",
                    )
                )
            } catch (e: Exception) {
                errors.add(
                    ImportError(
                        id = it.id,
                        filename = it.location.filename,
                        reason = e.message ?: "Unknown error",
                    )
                )
            }
        }
        if (errors.isNotEmpty()) {
            logger.error(errors)
            throw MusicImportException(errors)
        }
    }

    override fun deleteById(id: String) {
        with(musicRepository.getById(id)) {
            logger.debug("Deleting audio file ${getPath()}")
            Files.delete(getPath())
        }

        musicRepository.deleteById(id)
    }

}