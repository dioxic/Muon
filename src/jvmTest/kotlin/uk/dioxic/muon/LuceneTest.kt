package uk.dioxic.muon

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.TokenStream
import org.apache.lucene.analysis.core.LowerCaseFilter
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter
import org.apache.lucene.analysis.ngram.NGramTokenFilter
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.analysis.standard.StandardTokenizer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.FieldType
import org.apache.lucene.document.TextField
import org.apache.lucene.index.*
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.*
import org.apache.lucene.search.BooleanClause.Occur
import org.apache.lucene.store.ByteBuffersDirectory
import kotlin.test.Test

class LuceneTest {

    private val testData = listOf(
        TestData(artist = "DJ Zinc & DJ Friction", title = "Stepping Stones (DJ Zinc & DJ Friction Remix)"),
        TestData(artist = "FL Fiction", title = "The Catch (Feint & Fiction)"),
        TestData(artist = "Silent Killer & Tek Infection", title = "Chain Reaction"),
        TestData(artist = "BMotion", title = "Ignition"),
        TestData(artist = "Carlito & Addiction", title = "Perfect Combination"),
        TestData(artist = "Friction", title = "Someone"),
    )

    private val text = "friction"

    @Test
    fun standardAnalyzerTest() {
        val searcher = setup(testData, StandardAnalyzer())
        val query = QueryParser("search", StandardAnalyzer()).parse(text)

        val results = searcher.searchWithResults(query)

        results.forEach {
            println(it)
        }
    }

    @Test
    fun nGramAnalyserTest() {
        val searcher = setup(testData, nGramAnalyzer)
        val query = QueryParser("search", nGramAnalyzer).parse(text)

        val results = searcher.searchWithResults(query)

        results.forEach {
            println(it)
        }
    }

    @Test
    fun phraseQueryTest() {
        val searcher = setup(testData, StandardAnalyzer())
        val phraseQuery = PhraseQuery(3, "search", text, "zinc")
//        val query = QueryParser("search", customAnalyzer).parse("fricton")

        val results = searcher.searchWithResults(phraseQuery)

        results.forEach {
            println(it)
        }
    }

    @Test
    fun nGramWithBoost() {
        val searcher = setup(testData, nGramAnalyzer)
        val query = BooleanQuery.Builder()
            .add(QueryParser("search", nGramAnalyzer).parse(QueryParser.escape(text)), Occur.MUST)
            .add(FuzzyQuery(Term("search", text)), Occur.SHOULD)
            .build()

        val results = searcher.searchWithResults(query)

        results.forEach {
            println(it)
        }
    }

    @Test
    fun fuzzyQueryTest() {
        val searcher = setup(testData, nGramAnalyzer)
        val query = BooleanQuery.Builder()
            .add(FuzzyQuery(Term("search", text)), Occur.MUST)
            .build()

        val results = searcher.searchWithResults(query)

        results.forEach {
            println(it)
        }
    }

    @Test
    fun edgeGramWithBoost() {
        val searcher = setup(testData, edgeGramAnalyzer)
        val query = BooleanQuery.Builder()
            .add(QueryParser("search", edgeGramAnalyzer).parse(QueryParser.escape(text)), Occur.MUST)
            .add(FuzzyQuery(Term("search", text)), Occur.SHOULD)
            .build()

        val results = searcher.searchWithResults(query)

        results.forEach {
            println(it)
        }
    }

    private fun IndexSearcher.searchWithResults(query: Query): List<TestResult> =
        this.search(query, 10)
            .scoreDocs
            .map { this.storedFields().document(it.doc) to it.score }
            .map { (doc, score) -> TestResult("${doc["artist"]} - ${doc["title"]}", score) }

    private fun setup(testData: List<TestData>, analyzer: Analyzer): IndexSearcher {
        val directory = ByteBuffersDirectory()
        val indexWriter = IndexWriter(directory, IndexWriterConfig())
        testData.forEach {
            indexWriter.addDocument(it.document(analyzer))
        }
        indexWriter.forceMerge(1)

        val directoryReader = DirectoryReader.open(indexWriter)
        indexWriter.close()

        return IndexSearcher(directoryReader)
    }


}

val edgeGramAnalyzer = object : Analyzer() {
    override fun createComponents(fieldName: String?): TokenStreamComponents {
        val source = StandardTokenizer()
        val tokenStream = source
            .add { LowerCaseFilter(it) }
            .add { ASCIIFoldingFilter(it) }
            .add { EdgeNGramTokenFilter(it, 3, 5, true) }

        return TokenStreamComponents(source, tokenStream)
    }

    private fun TokenStream.add(block: (TokenStream) -> TokenStream) = block(this)
}

val nGramAnalyzer = object : Analyzer() {
    override fun createComponents(fieldName: String?): TokenStreamComponents {
        val source = StandardTokenizer()
        val tokenStream = source
            .add { LowerCaseFilter(it) }
            .add { ASCIIFoldingFilter(it) }
            .add { NGramTokenFilter(it, 3, 5, true) }

        return TokenStreamComponents(source, tokenStream)
    }

    private fun TokenStream.add(block: (TokenStream) -> TokenStream) = block(this)
}

private val searchFieldType = FieldType().apply {
    setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS)
    setTokenized(true)
    freeze()
}

fun searchField(name: String, value: String, analyzer: Analyzer) =
    Field(name, analyzer.tokenStream(name, value), searchFieldType)

data class TestData(val title: String, val artist: String) {
    fun document(analyzer: Analyzer) = Document().apply {
        add(searchField("search", "$title $artist", analyzer))
        add(TextField("title", title, Field.Store.YES))
        add(TextField("artist", artist, Field.Store.YES))
    }
}

data class TestResult(
    val text: String,
    val score: Float
)