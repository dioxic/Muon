package uk.dioxic.muon.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
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
import org.apache.lucene.search.BooleanClause.Occur.*
import org.apache.lucene.store.FSDirectory
import uk.dioxic.muon.audio.AudioDetails
import uk.dioxic.muon.audio.AudioFile
import uk.dioxic.muon.common.Global.homePath
import uk.dioxic.muon.exceptions.IdNotFoundException
import uk.dioxic.muon.isAudioFile
import uk.dioxic.muon.model.Library
import uk.dioxic.muon.removeProblemCharacters
import uk.dioxic.muon.toAudioFile
import uk.dioxic.muon.toDocument
import java.io.Closeable
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime


class MusicRepositoryImpl(indexPath: String) : MusicRepository, Closeable {

    private val logger = LogManager.getLogger()
    private val indexDirectory = FSDirectory.open(homePath.resolve(indexPath))
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

    fun dropIndex() {
        indexDirectory.deletePendingFiles()
        indexDirectory.listAll()
            .forEach(indexDirectory::deleteFile)
    }

    override fun size(): Int = DirectoryReader.open(indexDirectory).getDocCount("id")

    @Throws(IdNotFoundException::class)
    override fun getById(id: String) =
        withSearcher {
            search(queryById(id), 1).scoreDocs.getAudioFile(this).firstOrNull() ?: throw IdNotFoundException(id)
        }

    override fun search(
        query: Query,
        maxResults: Int,
        sortField: String,
        sortReverse: Boolean
    ) = withSearcher {
        search(
            query,
            maxResults,
            Sort(SortField("${sortField}_sort", SortField.Type.STRING, sortReverse))
        ).scoreDocs.getAudioDetails(this)
    }

    override fun search(query: Query, maxResults: Int) = withSearcher {
        search(query, maxResults).scoreDocs.getAudioDetails(this)
    }

    override fun searchAfter(query: Query, maxResults: Int, after: Int) = withSearcher {
        searchAfter(FieldDoc(after, 0.0f), query, maxResults).scoreDocs.getAudioDetails(this)
    }

    override fun searchAfter(
        query: Query,
        maxResults: Int,
        after: Int,
        sortField: String,
        sortReverse: Boolean
    ) = withSearcher {
        searchAfter(
            FieldDoc(after, 0.0f),
            query,
            maxResults,
            Sort(SortField("${sortField}_sort", SortField.Type.STRING, sortReverse))
        ).scoreDocs.getAudioDetails(this)
    }

    override fun getDuplicates(audioFiles: List<AudioFile>) = withSearcher {
        audioFiles.map { audioFile ->
            search(audioFile.duplicateQuery, 5)
                .scoreDocs
                .filter { it.score > 10f }
                .getAudioDetails(this)
        }
    }

    override fun deleteById(id: String) {
        indexWriter.deleteDocuments(Term("id", id))
    }

    @FlowPreview
    @ExperimentalTime
    override suspend fun refreshIndex(library: Library): Int {
        val dir = File(library.path)
        require(dir.isDirectory) { "${dir.name} is not a directory!" }

        logger.info("Refreshing index for library [${library.name}]...")

        val added: Int
        val deleted: Int
        val elapsed: Duration
        val searcher: IndexSearcher = searcherManager.acquire()

        try {
            elapsed = measureTime {
                added = addToIndex(library, searcher)
                deleted = pruneIndex(library, searcher)
            }
        } finally {
            searcherManager.release(searcher)
        }

        indexWriter.commit()
        searcherManager.maybeRefreshBlocking()

        logger.info("Completed index refresh for library [${library.name}] in $elapsed - $added files added, $deleted files removed")

        return added + deleted
    }

    @FlowPreview
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
//            .flatMapMerge(8) {
//                flow {
//                    if (searcher.search(it.query, 1).totalHits.value == 0)
//                        emit(it)
//                }
//            }
            .filterNot {
                searcher.search(it.query, 1).totalHits.value > 0
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

    @FlowPreview
    private suspend fun pruneIndex(library: Library, searcher: IndexSearcher): Int {
        var count = 0
        searcher.search(TermQuery(Term("library", library.id)), Int.MAX_VALUE).scoreDocs
            .asFlow()
            .map { searcher.doc(it.doc) }
            .flatMapMerge(50) {
                flow {
                    if (Files.notExists(Path.of(it.get("path")).resolve(it.get("filename"))))
                        emit(it)
                }
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

    override fun close() {
        searcherManager.close()
        indexWriter.close()
        indexDirectory.close()
    }

    private data class FileAndSize(
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

    private val AudioFile.duplicateQuery: Query
        get() = BooleanQuery.Builder()
            .add(
                MultiFieldQueryParser(arrayOf("title", "artist", "lyricist"), StandardAnalyzer())
                    .parse("${this.tags.title} ${this.tags.artist} ${this.tags.lyricist}".removeProblemCharacters()),
                MUST
            )
            .add(queryById(this.id), MUST_NOT)
            .build()

//    private fun IndexSearcher.searchAudio(query: Query, maxResults: Int): List<AudioFileMatch> =
//        this.search(query, maxResults).scoreDocs.map {
//            AudioFileMatch(
//                audioFile = this.doc(it.doc).toAudioFile(),
//                matchScore = it.score
//            )
//        }
//
//    private fun Array<ScoreDoc>.getAudioMatches(searcher: IndexSearcher) =
//        map {
//            AudioFileMatch(
//                audioFile = searcher.doc(it.doc).toAudioFile(),
//                matchScore = it.score
//            )
//        }

    private fun Array<ScoreDoc>.getAudioDetails(searcher: IndexSearcher) =
        map { it.toAudioDetails(searcher) }

    private fun List<ScoreDoc>.getAudioDetails(searcher: IndexSearcher) =
        map { it.toAudioDetails(searcher) }

    private fun ScoreDoc.toAudioDetails(searcher: IndexSearcher) =
        AudioDetails(
            audioFile = searcher.doc(doc).toAudioFile(),
            score = if (score.isNaN()) null else score,
            docId = doc,
        )

    private fun Array<ScoreDoc>.getAudioFile(searcher: IndexSearcher) =
        map {
            searcher.doc(it.doc).toAudioFile()
        }

    private fun List<ScoreDoc>.getAudioFile(searcher: IndexSearcher) =
        map {
            searcher.doc(it.doc).toAudioFile()
        }

    private fun <T> withSearcher(block: IndexSearcher.() -> T): T {
        val searcher = searcherManager.acquire()
        try {
            return block.invoke(searcher)
        } finally {
            searcherManager.release(searcher)
        }
    }

}