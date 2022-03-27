package uk.dioxic.muon

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.*
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.Term
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.queryparser.surround.query.FieldsQuery
import org.apache.lucene.search.*
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.util.BytesRef
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class LuceneTest {}
//
//    @Test
//    fun libraryIndexTest() {
//        val indexLocation = Global.homePath.resolve("textIndex")
//        val indexDirectory = FSDirectory.open(indexLocation)
//        val indexWriter = IndexWriter(
//            indexDirectory,
//            IndexWriterConfig(StandardAnalyzer())
//        )
//
//        File("J:\\import\\complete").walk()
//            .filter { it.isAudioFile }
//            .map {
//                it
//            }
//            .map { it.toAudioFile() }
//            .forEach { audio ->
//                val doc = Document().apply {
//                    add(StringField("id", audio.id, Field.Store.YES))
//                    add(TextField("artist", audio.tags.artist, Field.Store.YES))
//                    add(TextField("title", audio.tags.title, Field.Store.YES))
//                    add(TextField("lyricist", audio.tags.lyricist, Field.Store.YES))
//                    add(TextField("album", audio.tags.album, Field.Store.YES))
//                    add(StringField("library", "test", Field.Store.YES))
//                    add(StringField("comment", audio.tags.comment, Field.Store.YES))
//                    add(StringField("genre", audio.tags.genre, Field.Store.YES))
//                    add(StringField("year", audio.tags.year, Field.Store.YES))
//                    add(StringField("fileType", audio.header.fileType, Field.Store.YES))
//                    add(StringField("bitrate", audio.header.bitrate.toString(), Field.Store.YES))
//                    add(StringField("vbr", audio.header.vbr.toString(), Field.Store.YES))
//                    add(StringField("length", audio.header.length.toString(), Field.Store.YES))
//                    add(StringField("path", audio.location.path, Field.Store.YES))
//                    add(StringField("filename", audio.location.filename, Field.Store.YES))
//                    add(StringField("filesize", Files.size(audio.getPath()).toString(), Field.Store.NO))
//
//                    add(SortedDocValuesField("title_sort", BytesRef(audio.tags.title)))
//                    add(SortedDocValuesField("artist_sort", BytesRef(audio.tags.artist)))
//                }
//                indexWriter.addDocument(audio.toDocument("test"))
////                indexWriter.addDocument(doc)
//            }
//
//        indexWriter.commit()
//        indexWriter.close()
//
//        val reader = DirectoryReader.open(indexDirectory)
//        val searcher = IndexSearcher(reader)
//
////        val query = QueryParser("#library:import", StandardAnalyzer())
//        val query = TermQuery(Term("library", "test"))
//
//        val topDocs = searcher.search(
//            query,
//            100,
//            Sort(SortField("title_sort", SortField.Type.STRING))
//        )
//
//        val result = topDocs.scoreDocs.map {
//            it.doc to searcher.doc(it.doc)
//        }
//
//        result.forEach {
//            println("docId: ${it.first}, title: ${it.second["title"]}")
//        }
//
//        reader.close()
//        indexDirectory.close()
//
//        Files.walk(indexLocation)
//            .sorted(Comparator.reverseOrder())
//            .forEach { Files.delete(it) }
//
//    }
//
//
//    @Test
//    fun sortingTest() {
//        val indexLocation = Global.homePath.resolve("testIndex")
//        val indexDirectory = FSDirectory.open(indexLocation)
//        val indexWriter = IndexWriter(
//            indexDirectory,
//            IndexWriterConfig(StandardAnalyzer())
//        )
//
//        val animals = listOf("badger", "fish", "turkey")
//
//        val text = (0..10).map {
//            val animal = animals[Random.nextInt(0, 2)]
//            "${Random.nextInt(0, 100)} $animal"
//        }
//
//        val docs = List(text.size) { i ->
//            Document().also { doc ->
//                doc.add(StringField("id", i.toString(), Field.Store.YES))
//                doc.add(TextField("text", text[i], Field.Store.YES))
//                doc.add(SortedDocValuesField("text_sorted", BytesRef(text[i])))
//            }
//        }
//
//        docs.forEach { indexWriter.addDocument(it) }
//
//        indexWriter.commit()
//        indexWriter.close()
//
//        val reader = DirectoryReader.open(indexDirectory)
//        val searcher = IndexSearcher(reader)
//        val query = FuzzyQuery(Term("text", "badger"))
//
//        val query2 = BooleanQuery.Builder()
//            .add(
//                MultiFieldQueryParser(arrayOf("id", "text"), StandardAnalyzer()).parse("badger"),
//                BooleanClause.Occur.MUST
//            )
//            .build()
//
//        val topDocs = searcher.search(
//            query2,
//            100,
//            Sort(SortField("text_sorted", SortField.Type.STRING))
//        )
//
//        val result = topDocs.scoreDocs.map {
//            it.doc to searcher.doc(it.doc)
//        }
//
//        result.forEach {
//            println("docId: ${it.first}, text: ${it.second["text"]}")
//        }
//
//        reader.close()
//        indexDirectory.close()
//
//        Files.walk(indexLocation)
//            .sorted(Comparator.reverseOrder())
//            .forEach { Files.delete(it) }
//
//        assertEquals(text.filter { it.contains("badger") }.sorted(), result.map { it.second["text"] })
//
//    }
//
//}