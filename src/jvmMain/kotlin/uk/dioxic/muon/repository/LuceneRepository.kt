package uk.dioxic.muon.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import org.apache.logging.log4j.LogManager
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.Term
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.*
import org.apache.lucene.search.BooleanClause.Occur
import org.apache.lucene.store.MMapDirectory
import uk.dioxic.muon.lucene.NGramAnalyser
import uk.dioxic.muon.lucene.charArraySetOf
import uk.dioxic.muon.lucene.toDocument
import uk.dioxic.muon.model.Track
import java.io.Closeable
import java.nio.file.Path


class LuceneRepository(indexPath: Path) : Closeable {
    private val logger = LogManager.getLogger()
    private val stopWords = charArraySetOf("and", "ft", "feat")
    private val searchAnalyser = NGramAnalyser(stopWords)
    private val directory = MMapDirectory.open(indexPath)
    private val indexWriter: IndexWriter = IndexWriter(
        directory,
        IndexWriterConfig(StandardAnalyzer(stopWords))
    )
    private val searcherManager = SearcherManager(indexWriter, SearcherFactory())

    init {
        indexWriter.commit()
    }

    fun search(
        text: String?,
        maxResults: Int
    ) = searcherManager.use { searcher ->
        searcher.search(query(text), maxResults)
            .scoreDocs
            .map { searcher.doc(it.doc, setOf("id")).get("id") }
//            .map { searcher.doc(it.doc, setOf("id")).get("id") to it.score }
    }

    suspend fun upsert(tracks: Flow<Track>): Int {
        var count = 0
        tracks.collect { track ->
            logger.trace("upserting lucene index with ${track.filename}")
            indexWriter.updateDocument(Term("id", track.id), track.toDocument(searchAnalyser))
            count++
        }
        indexWriter.commit()
        searcherManager.maybeRefresh()
        logger.debug("updated index for $count tracks")
        return count
    }

//    fun dropIndex() {
//        indexDirectory.deletePendingFiles()
//        indexDirectory.listAll()
//            .forEach(indexDirectory::deleteFile)
//    }

    override fun close() {
        searcherManager.close()
        indexWriter.close()
        directory.close()
    }

    private inline fun <T> SearcherManager.use(block: (IndexSearcher) -> T): T {
//        contract {
//            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//        }
        val searcher = this.acquire()
        try {
            return block(searcher)
        } finally {
            this.release(searcher)
        }
    }

    private fun query(
        text: String? = null
    ): Query =
        if (text == null || text.isBlank()) {
            MatchAllDocsQuery()
        } else {
            BooleanQuery.Builder()
                .add(
                    QueryParser("search", searchAnalyser).parse(QueryParser.escape(text)),
                    Occur.MUST
                )
                .add(FuzzyQuery(Term("search", text)), Occur.SHOULD)
                .build()
        }

}