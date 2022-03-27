package uk.dioxic.muon.service

import org.apache.logging.log4j.LogManager
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.Term
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.*
import org.jaudiotagger.audio.AudioFileIO
import uk.dioxic.muon.audio.AudioFile
import uk.dioxic.muon.audio.ImportError
import uk.dioxic.muon.audio.AudioDetails
import uk.dioxic.muon.exceptions.MusicImportException
import uk.dioxic.muon.getPath
import uk.dioxic.muon.merge
import uk.dioxic.muon.nullIfBlank
import uk.dioxic.muon.repository.MusicRepository
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.io.path.extension

class OldMusicServiceImpl(private val musicRepository: MusicRepository) : MusicRepository by musicRepository,
    OldMusicService {

    private val logger = LogManager.getLogger()

    override fun attachDuplicates(audioList: List<AudioDetails>): List<AudioDetails> =
        getDuplicates(audioList.map { it.audioFile })
            .mapIndexed { index, audioFiles ->
                audioList[index].copy(
                    duplicates = audioFiles
                )
            }

    override fun search(
        libraryId: String?,
        text: String?,
        searchFields: Array<String>,
        maxResults: Int,
        after: Int?,
        sortField: String?,
        sortReverse: Boolean
    ): List<AudioDetails> {
        val query = query(libraryId, text, searchFields)

        return if (sortField != null) {
            if (after != null) {
                searchAfter(query, maxResults, after, sortField, sortReverse)
            } else {
                search(query, maxResults, sortField, sortReverse)
            }
        } else {
            if (after != null) {
                searchAfter(query, maxResults, after)
            } else {
                search(query, maxResults)
            }
        }
    }

    override fun update(libraryId: String?, audioFile: AudioFile) {
        val cachedFile = musicRepository.getById(audioFile.id)
        val overwrite = false

        if (audioFile.location != cachedFile.location) {
            logger.debug("Moving/renaming file ${audioFile.location.filename}")

            require(audioFile.getPath().extension.isNotEmpty()) { "Missing file extension" }

            if (overwrite) {
                Files.move(cachedFile.getPath(), audioFile.getPath(), StandardCopyOption.REPLACE_EXISTING)
            } else if (cachedFile.location.path == audioFile.location.path) {
                // hack to deal with file rename where the case-insensitive filename doesn't change
                Files.move(cachedFile.getPath(), audioFile.getPath(), StandardCopyOption.ATOMIC_MOVE)
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

    private fun query(
        libraryId: String? = null,
        text: String? = null,
        fields: Array<String>
    ): Query =
        if (libraryId == null && text == null) {
            MatchAllDocsQuery()
        } else {
            BooleanQuery.Builder().let { builder ->
                libraryId?.let { builder.add(TermQuery(Term("library", libraryId)), BooleanClause.Occur.FILTER) }
                text.nullIfBlank()?.let {
                    val queryParser = MultiFieldQueryParser(fields, StandardAnalyzer())
                    queryParser.defaultOperator = QueryParser.Operator.AND
                    builder.add(queryParser.parse(it), BooleanClause.Occur.MUST)
                }
                builder.build()
            }
        }

}