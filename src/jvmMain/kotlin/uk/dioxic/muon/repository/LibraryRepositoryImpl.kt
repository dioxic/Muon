package uk.dioxic.muon.repository

import org.apache.logging.log4j.LogManager
import uk.dioxic.muon.model.Library

class LibraryRepositoryImpl(private val configRepository: ConfigRepository) : LibraryRepository {

    private val logger = LogManager.getLogger()

    private var libraryCache: MutableMap<String, Library> =
        configRepository.getLibraryConfig().libraries.associateBy { it.id }.toMutableMap()

    override fun getLibraryById(libraryId: String) = libraryCache[libraryId] ?: error("Library '$libraryId' not found!")

    override fun getLibraryByPath(path: String) = libraryCache.values.first { it.path.startsWith(path) }

    override fun getLibraries(): List<Library> = libraryCache.values.toList()

    override fun saveLibrary(library: Library) {
        logger.debug("Saving library '${library.name}'")
        libraryCache[library.id] = library
        saveLibaryConfig()
    }

    override fun deleteLibrary(libraryId: String) {
        logger.debug("Deleting library '$libraryId'")
        libraryCache.remove(libraryId)
        saveLibaryConfig()
    }

    private fun saveLibaryConfig() =
        configRepository.save(
            configRepository
                .getLibraryConfig()
                .copy(libraries = libraryCache.values.toList())
        )

}