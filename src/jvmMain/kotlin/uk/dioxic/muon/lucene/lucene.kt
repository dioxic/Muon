package uk.dioxic.muon.lucene

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.CharArraySet
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.apache.lucene.document.*
import org.apache.lucene.index.IndexOptions
import uk.dioxic.muon.common.toEpochSecondsUtc
import uk.dioxic.muon.model.Track


fun charArraySetOf(vararg item: String) =
    CharArraySet(item.toList(), false)

private val searchFieldType = FieldType().apply {
    setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS)
    setTokenized(true)
    freeze()
}

fun Track.toDocument(searchAnalyser: Analyzer): Document = Document().apply {
    add(
        Field(
            "search",
            searchAnalyser.tokenStream("search", "$artist $title $lyricist"), searchFieldType
        )
    )
    add(StringField("id", id, Field.Store.YES))
    add(TextField("artist", artist, Field.Store.NO))
    add(TextField("title", title, Field.Store.NO))
    add(TextField("lyricist", lyricist, Field.Store.NO))
    add(TextField("album", album, Field.Store.NO))
    add(TextField("comment", comment, Field.Store.NO))
    add(StringField("genre", genre, Field.Store.NO))
    add(StringField("year", year, Field.Store.NO))
    add(StringField("fileType", type.toString(), Field.Store.NO))
    add(SortedNumericDocValuesField("createDate", createDate.toEpochSecondsUtc()))
    add(IntPoint("bitrate", bitrate))
    add(IntPoint("length", length))
    add(StringField("path", path, Field.Store.NO))
    add(StringField("filename", filename, Field.Store.NO))
    add(IntPoint("filesize", fileSize))
    key?.let { add(StringField("key", key, Field.Store.NO)) }
    rating?.let { add(IntPoint("rating", it)) }
    bpm?.let { add(IntPoint("bpm", it)) }
    color?.apply { add(StringField("color", name, Field.Store.NO)) }
}

fun analyze(text: String, analyzer: Analyzer): List<String> {
    val result = mutableListOf<String>()
    analyzer.tokenStream("FIELD_NAME", text).use { tokenStream ->
        val attr = tokenStream.addAttribute(CharTermAttribute::class.java)
        tokenStream.reset()
        while (tokenStream.incrementToken()) {
            result.add(attr.toString())
        }
        return result
    }
}