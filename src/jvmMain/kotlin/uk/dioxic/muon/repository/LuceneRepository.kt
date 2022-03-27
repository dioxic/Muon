package uk.dioxic.muon.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.forEach
import org.apache.logging.log4j.LogManager
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.*
import org.apache.lucene.document.Field.Store
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.Term
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.*
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.util.BytesRef
import uk.dioxic.muon.model.Track
import uk.dioxic.muon.nullIfBlank
import java.io.Closeable
import java.nio.file.Path

class LuceneRepository(indexPath: Path) : Closeable {

    private val logger = LogManager.getLogger()
    private val indexDirectory = FSDirectory.open(indexPath)
    private val indexWriter: IndexWriter = IndexWriter(
        indexDirectory,
        IndexWriterConfig(StandardAnalyzer())
    )
    private val searcherManager = SearcherManager(indexWriter, SearcherFactory())

    init {
        indexWriter.commit()
    }

    fun search(
        text: String?,
        maxResults: Int,
        fields: Array<String> = arrayOf("artist", "title", "lyricist")
    ) = searcherManager.use { searcher ->
        searcher.search(query(text, fields), maxResults)
            .scoreDocs
            .map { searcher.doc(it.doc, setOf("id")) }
            .map { it.get("id") }
    }

    suspend fun upsert(tracks: Flow<Track>): Int {
        var count = 0
        tracks.collect { track ->
            logger.trace("upserting lucene index with ${track.filename}")
            indexWriter.updateDocument(Term("id", track.id), track.toDocument())
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
        indexDirectory.close()
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
        text: String? = null,
        fields: Array<String>
    ): Query =
        if (text == null || text.isBlank()) {
            MatchAllDocsQuery()
        } else {
            BooleanQuery.Builder().let { builder ->
                text.nullIfBlank()?.let {
                    val queryParser = MultiFieldQueryParser(fields, StandardAnalyzer())
                    queryParser.defaultOperator = QueryParser.Operator.AND
                    builder.add(queryParser.parse(it), BooleanClause.Occur.MUST)
                }
                builder.build()
            }
        }

    private fun Track.toDocument(): Document = Document().also {
        it.add(StringField("id", id, Store.YES))
        it.add(TextField("artist", artist, Store.NO))
        it.add(TextField("title", title, Store.NO))
        it.add(TextField("lyricist", lyricist, Store.NO))
        it.add(TextField("album", album, Store.NO))
        it.add(StringField("comment", comment, Store.NO))
        it.add(StringField("genre", genre, Store.NO))
        it.add(IntPoint("year", year))
        it.add(StringField("fileType", fileType.toString(), Store.NO))
        it.add(IntPoint("bitrate", bitrate))
        it.add(IntPoint("length", length))
        it.add(StringField("path", path, Store.NO))
        it.add(StringField("filename", filename, Store.NO))
        it.add(IntPoint("filesize", fileSize))

        it.add(SortedDocValuesField("artist_sort", BytesRef(artist)))
        it.add(SortedDocValuesField("title_sort", BytesRef(title)))
        it.add(SortedDocValuesField("lyricist_sort", BytesRef(lyricist)))
        it.add(SortedDocValuesField("album_sort", BytesRef(album)))
    }
}