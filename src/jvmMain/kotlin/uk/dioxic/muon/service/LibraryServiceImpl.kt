package uk.dioxic.muon.service

import org.apache.logging.log4j.LogManager
import uk.dioxic.muon.audio.AudioFile
import uk.dioxic.muon.audio.ImportError
import uk.dioxic.muon.config.Library
import uk.dioxic.muon.getPath
import uk.dioxic.muon.repository.ConfigRepository
import uk.dioxic.muon.repository.LibraryRepository
import java.nio.file.CopyOption
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.io.path.extension

class LibraryServiceImpl(
    private val libraryRepository: LibraryRepository,
    private val configRepository: ConfigRepository
) : LibraryService {

    private val logger = LogManager.getLogger()

    private var libraryCache: MutableMap<String, Library> =
        configRepository.getLibraryConfig().libraries.associateBy { it.name }.toMutableMap()

    private var fileCache: MutableMap<String, MutableMap<String, AudioFile>> = mutableMapOf()

    override fun reload(libraryId: String) {
        logger.debug("Reloading library files for '$libraryId'")
        fileCache[libraryId] = libraryRepository.getFiles(getLibrary(libraryId)).associateBy { it.id }.toMutableMap()
    }

    override fun getFiles(libraryId: String): List<AudioFile> {
        logger.debug("Retrieving files for library '$libraryId'")
        if (!fileCache.containsKey(libraryId)) {
            reload(libraryId)
        }
        return fileCache[libraryId]!!.values.toList()
    }

    override fun getLibrary(libraryId: String) = libraryCache[libraryId] ?: error("Library '$libraryId' not found!")

    override fun saveLibrary(library: Library) {
        logger.debug("Saving library '${library.name}'")
        libraryCache[library.name] = library
        saveLibaryConfig()
    }

    override fun deleteLibrary(libraryId: String) {
        logger.debug("Deleting library '$libraryId'")
        libraryCache.remove(libraryId)
        saveLibaryConfig()
    }

    override fun saveFile(audioFile: AudioFile, overwrite: Boolean) {
        val (library, cachedFile) = getFile(audioFile.id)

        if (audioFile.location != cachedFile.location) {
            logger.debug("Moving file ${cachedFile.getPath()} to ${audioFile.getPath()}")

            require(audioFile.getPath().extension.isNotEmpty()) { "Missing file extension" }

            if (overwrite) {
                Files.move(cachedFile.getPath(), audioFile.getPath(), StandardCopyOption.REPLACE_EXISTING)
            } else {
                Files.move(cachedFile.getPath(), audioFile.getPath())
            }
        }

        if (audioFile.tags != cachedFile.tags) {
            logger.debug("Saving tags for file ${cachedFile.getPath()}")
            libraryRepository.saveTags(audioFile)
        }

        library[audioFile.id] = audioFile
    }

    override fun saveFiles(audioFiles: List<AudioFile>, overwrite: Boolean): List<ImportError> {
        val errors = mutableListOf<ImportError>()
        audioFiles.forEach {
            try {
                saveFile(it)
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
        }
        return errors
    }

    override fun deleteFile(fileId: String) {
        val (library, cachedFile) = getFile(fileId)

        logger.debug("Deleting audio file ${cachedFile.getPath()}")
        Files.delete(cachedFile.getPath())
        library.remove(cachedFile.id)
    }

    private fun saveLibaryConfig() =
        configRepository.save(
            configRepository
                .getLibraryConfig()
                .copy(libraries = libraryCache.values.toList())
        )

    private fun getFile(id: String): Pair<MutableMap<String, AudioFile>, AudioFile> {
        fileCache.forEach { (_, fileMap) ->
            val file = fileMap[id]
            if (file != null) {
                return Pair(fileMap, file)
            }
        }
        error("Unknown audio file ID")
    }
}
