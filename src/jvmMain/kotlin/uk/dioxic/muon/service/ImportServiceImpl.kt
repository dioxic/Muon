package uk.dioxic.muon.service

import org.apache.logging.log4j.LogManager
import uk.dioxic.muon.audio.AudioFile
import uk.dioxic.muon.getPath
import uk.dioxic.muon.repository.LibraryRepository
import java.nio.file.Files

class ImportServiceImpl(private val libraryRepository: LibraryRepository) : ImportService {

    private val logger = LogManager.getLogger()

    private lateinit var fileCache: MutableMap<String, AudioFile>

    init {
        reload()
    }

    override fun reload() {
        fileCache = libraryRepository.getImportFiles().associateBy { it.id }.toMutableMap()
    }

    override fun getImportFiles() = fileCache.values.toList()

    override fun save(audioFile: AudioFile) {
        val cachedFile = fileCache[audioFile.id]

        requireNotNull(cachedFile) { "Unknown audio file ID"}

        if (audioFile.location != cachedFile.location) {
            logger.debug("Moving file ${cachedFile.getPath()} to ${audioFile.getPath()}")
            Files.move(cachedFile.getPath(), audioFile.getPath())
        }

        if (audioFile.tags != cachedFile.tags) {
            logger.debug("Saving tags for file ${cachedFile.getPath()}")
            libraryRepository.saveTags(audioFile)
        }

        fileCache[audioFile.id] = audioFile
    }

    override fun delete(id: String) {
        val cachedFile = fileCache[id]
        requireNotNull(cachedFile) { "Unknown audio file ID"}

        logger.debug("Deleting audio file ${cachedFile.getPath()}")
        Files.delete(cachedFile.getPath())
        fileCache.remove(cachedFile.id)
    }

}