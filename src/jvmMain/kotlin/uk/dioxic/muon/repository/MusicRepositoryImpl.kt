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
        indexWriter.deleteDocuments(audioFile.query())
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
    ): List<AudioFile> {
        val query = if (libraryId == null && text == null) {
            MatchAllDocsQuery()
        } else {
            BooleanQuery.Builder().let { builder ->
                libraryId?.let { builder.add(TermQuery(Term("library", libraryId)), FILTER) }
                text?.let { builder.add(MultiFieldQueryParser(fields, StandardAnalyzer()).parse(text), FILTER) }
                builder.build()
            }
        }

        return search(query, maxResults)
    }

    override fun deleteById(id: String) {
        indexWriter.deleteDocuments(Term("id", id))
    }

    override suspend fun refreshIndex(library: Library): Int {
        val dir = File(library.path)
        require(dir.isDirectory) { "${dir.name} is not a directory!" }

        logger.info("Refreshing index for library [${library.name}]...")

        var count = 0
        val searcher: IndexSearcher = searcherManager.acquire()

        try {
            dir.walk()
                .filter { it.isAudioFile }
                .asFlow()
                .map { it to Files.size(it.toPath()) }
                .filterNot {
                    searcher.search(
                        searchQuery(it.first.parent, it.first.name, it.second),
                        2
                    ).totalHits.value > 0
                }
                .map { it.first.toAudioFile() }
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
        } finally {
            searcherManager.release(searcher)
        }

        indexWriter.commit()
        searcherManager.maybeRefreshBlocking()

        logger.info("Completed index refresh for library [${library.name}] - $count files added")
        return count
    }

    private fun searchQuery(path: String, filename: String, filesize: Long) =
        BooleanQuery.Builder()
            .add(TermQuery(Term("path", path)), FILTER)
            .add(TermQuery(Term("filename", filename)), FILTER)
            .add(TermQuery(Term("filesize", filesize.toString())), FILTER)
            .build()

    private fun AudioFile.query() =
        BooleanQuery.Builder()
            .add(TermQuery(Term("path", this.location.path)), FILTER)
            .add(TermQuery(Term("filename", this.location.filename)), FILTER)
            .build()

    override fun close() {
        searcherManager.close()
        indexWriter.close()
        indexDirectory.close()
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

}