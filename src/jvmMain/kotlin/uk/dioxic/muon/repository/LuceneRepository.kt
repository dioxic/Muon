package uk.dioxic.muon.repository

import kotlinx.coroutines.flow.Flow
import org.apache.logging.log4j.kotlin.logger
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.Term
import org.apache.lucene.search.*
import org.apache.lucene.search.BooleanClause.Occur
import org.apache.lucene.store.MMapDirectory
import uk.dioxic.muon.lucene.analyze
import uk.dioxic.muon.lucene.charArraySetOf
import uk.dioxic.muon.lucene.toDocument
import uk.dioxic.muon.model.Track
import java.io.Closeable
import java.nio.file.Path


class LuceneRepository(indexPath: Path) : Closeable {
    private val logger = logger()
    private val stopWords = charArraySetOf("and", "ft", "feat", "dj")
    private val searchAnalyser = StandardAnalyzer(stopWords)
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
        track: Track,
        maxResults: Int
    ) = searcherManager.use { searcher ->
        searcher.search(query(track), maxResults)
            .scoreDocs
            .map { searcher.storedFields().document(it.doc, setOf("id")).get("id") }
    }

    fun search(
        text: String?,
        maxResults: Int
    ) = searcherManager.use { searcher ->
        when {
            text.isNullOrBlank() -> {
                val sort = Sort(SortedNumericSortField("createDate", SortField.Type.LONG, true))
                searcher.search(MatchAllDocsQuery(), maxResults, sort, false)
            }

            else -> searcher.search(query(text), maxResults)
        }
            .scoreDocs
            .map { searcher.storedFields().document(it.doc, setOf("id")).get("id") }
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

    fun deleteAll() {
        indexWriter.deleteAll()
        indexWriter.commit()
    }

    override fun close() {
        searcherManager.close()
        indexWriter.close()
        directory.close()
    }

    private inline fun <T> SearcherManager.use(block: (IndexSearcher) -> T): T {
        val searcher = this.acquire()
        try {
            return block(searcher)
        } finally {
            this.release(searcher)
        }
    }

    private fun query(track: Track) =
        with(BooleanQuery.Builder()) {
            add(PhraseQuery(1, "artist", *analyze(track.artist, searchAnalyser).toTypedArray()), Occur.MUST)
            add(PhraseQuery(1, "title", *analyze(track.title, searchAnalyser).toTypedArray()), Occur.MUST)
            add(PhraseQuery(1, "lyricist", *analyze(track.lyricist, searchAnalyser).toTypedArray()), Occur.SHOULD)
//            analyze(track.artist, searchAnalyser).forEach {
//                add(FuzzyQuery(Term("artist", it), 1), Occur.MUST)
//            }
//            analyze(track.title, searchAnalyser).forEach {
//                add(FuzzyQuery(Term("title", it), 1), Occur.MUST)
//            }
//            analyze(track.lyricist, searchAnalyser).forEach {
//                add(FuzzyQuery(Term("lyricist", it), 1), Occur.SHOULD)
//            }

            build()
        }

    private fun query(text: String): Query =
//            with(PhraseQuery.Builder()) {
//                setSlop(2)
//                text.split(Regex("\\s+")).forEach {
//                    logger.info("token: $it")
//                    add(Term("search", it))
//                }
//                build()
//            }
        with(BooleanQuery.Builder()) {
//                add(
//                    QueryParser("search", searchAnalyser).parse(QueryParser.escape(text)),
//                    Occur.SHOULD
//                )
            analyze(text, searchAnalyser).forEach {
                logger.info("token: $it")
                add(FuzzyQuery(Term("search", it), 2), Occur.MUST)
            }


            build()
        }

}