package uk.dioxic.muon.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.apache.logging.log4j.LogManager
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.Term
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser
import org.apache.lucene.search.*
import org.apache.lucene.search.BooleanClause.Occur.FILTER
import org.apache.lucene.store.FSDirectory
import uk.dioxic.muon.audio.AudioFile
import uk.dioxic.muon.exceptions.IdNotFoundException
import uk.dioxic.muon.isAudioFile
import uk.dioxic.muon.model.Library
import uk.dioxic.muon.muonHome
import uk.dioxic.muon.toAudioFile
import uk.dioxic.muon.toDocument
import java.io.Closeable
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


class MusicRepositoryImpl(indexPath: String) : MusicRepository, Closeable {

    private val logger = LogManager.getLogger()
    private val indexDirectory = FSDirectory.open(Paths.get(muonHome).resolve(indexPath))
    private val indexWriter: IndexWriter = IndexWriter(
        indexDirectory,
        IndexWriterConfig(StandardAnalyzer())
    )
    private val searcherManager = SearcherManager(indexWriter, SearcherFactory())

    init {
        indexWriter.commit()
    }

    val indexLocation: Path
        get() = indexDirectory.directory

    override fun updateMany(libraryId: String?, audioFiles: List<AudioFile>) =
        audioFiles.forEach { update(libraryId, it) }

    override fun update(libraryId: String?, audioFile: AudioFile) {
        val resolvedLibraryId =
            libraryId ?: audioFile.indexedLibrary ?: error("file ${audioFile.location.filename} not indexed")

        logger.trace("updating index for ${audioFile.location.filename}")
        indexWriter.updateDocument(Term("id", audioFile.id), audioFile.toDocument(resolvedLibraryId))
        indexWriter.commit()
        searcherManager.maybeRefresh()
    }

    private fun save(libraryId: String, audioFile: AudioFile, indexWriter: IndexWriter) {
        logger.trace("indexing ${audioFile.location.filename}")
        indexWriter.deleteDocuments(audioFile.filenameQuery)
        indexWriter.addDocument(audioFile.toDocument(libraryId))
    }

    private fun queryById(id: String): Query = TermQuery(Term("id", id))

    private fun search(query: Query, maxResults: Int): List<AudioFile> {
        val searcher = searcherManager.acquire()
        try {
            return searcher.search(query, maxResults).scoreDocs
                .map {
                    searcher
                        .doc(it.doc)
                        .toAudioFile()
                }
        } finally {
            searcherManager.release(searcher)
        }
    }

    fun dropIndex() {
        indexDirectory.deletePendingFiles()
        indexDirectory.listAll()
            .forEach(indexDirectory::deleteFile)
    }

    override fun size(): Int = DirectoryReader.open(indexDirectory).getDocCount("id")

    @Throws(IdNotFoundException::class)
    override fun getById(id: String) =
        search(queryById(id), maxResults = 1).firstOrNull() ?: throw IdNotFoundException(id)

    override fun search(
        libraryId: String?,
        text: String?,
        maxResults: Int,
        fields: Array<String>
    ): List<AudioFile> = search(
        query(
            libraryId = libraryId,
            text = text,
            fields = fields
        ), maxResults
    )

    override fun deleteById(id: String) {
        indexWriter.deleteDocuments(Term("id", id))
    }

    override suspend fun refreshIndex(library: Library): Int {
        val dir = File(library.path)
        require(dir.isDirectory) { "${dir.name} is not a directory!" }

        logger.info("Refreshing index for library [${library.name}]...")

        val added: Int
        val deleted: Int
        val searcher: IndexSearcher = searcherManager.acquire()

        try {
            added = addToIndex(library, searcher)
            deleted = pruneIndex(library, searcher)
        } finally {
            searcherManager.release(searcher)
        }

        indexWriter.commit()
        searcherManager.maybeRefreshBlocking()

        logger.info("Completed index refresh for library [${library.name}] - $added files added, $deleted files removed")

        return added + deleted
    }

    private suspend fun addToIndex(library: Library, searcher: IndexSearcher): Int {
        val dir = File(library.path)
        require(dir.isDirectory) { "${dir.name} is not a directory!" }

        var count = 0

        dir.walk()
            .filter { it.isAudioFile }
            .asFlow()
            .map {
                FileAndSize(it, Files.size(it.toPath()))
            }
            .filterNot {
                searcher.search(it.query, 2).totalHits.value > 0
            }
            .map { it.file.toAudioFile() }
            .flowOn(Dispatchers.IO)
            .onEach {
                count++
                if (count % 100 == 0) {
                    logger.debug("indexed $count files")
                }
            }
            .collect {
                save(library.id, it, indexWriter)
            }

        return count
    }

    private suspend fun pruneIndex(library: Library, searcher: IndexSearcher): Int {
        var count = 0
        searcher.search(query(libraryId = library.id), Int.MAX_VALUE).scoreDocs
            .asFlow()
            .map { searcher.doc(it.doc) }
            .filter {
                Files.notExists(Path.of(it.get("path")).resolve(it.get("filename")))
            }
            .flowOn(Dispatchers.IO)
            .onEach {
                count++
                if (count % 100 == 0) {
                    logger.debug("pruned $count files")
                }
            }
            .collect {
                deleteById(it.get("id"))
            }
        return count
    }

    private fun query(
        libraryId: String? = null,
        text: String? = null,
        fields: Array<String> = emptyArray()
    ): Query =
        if (libraryId == null && text == null) {
            MatchAllDocsQuery()
        } else {
            BooleanQuery.Builder().let { builder ->
                libraryId?.let { builder.add(TermQuery(Term("library", libraryId)), FILTER) }
                text?.let { builder.add(MultiFieldQueryParser(fields, StandardAnalyzer()).parse(text), FILTER) }
                builder.build()
            }
        }

    override fun close() {
        searcherManager.close()
        indexWriter.close()
        indexDirectory.close()
    }

    data class FileAndSize(
        val file: File,
        val size: Long
    ) {
        val query: Query
            get() = BooleanQuery.Builder()
                .add(TermQuery(Term("path", file.parent)), FILTER)
                .add(TermQuery(Term("filename", file.name)), FILTER)
                .add(TermQuery(Term("filesize", size.toString())), FILTER)
                .build()
    }

    private fun AudioFile.indexedDocument(): Document? {
        val searcher = searcherManager.acquire()
        try {
            return searcher.search(queryById(this.id), 1).scoreDocs
                .map { searcher.doc(it.doc) }
                .firstOrNull()
        } finally {
            searcherManager.release(searcher)
        }
    }

    private val AudioFile.indexedLibrary: String?
        get() = this.indexedDocument()?.get("library")

    private val AudioFile.filenameQuery: Query
        get() = BooleanQuery.Builder()
            .add(TermQuery(Term("path", this.location.path)), FILTER)
            .add(TermQuery(Term("filename", this.location.filename)), FILTER)
            .build()

}