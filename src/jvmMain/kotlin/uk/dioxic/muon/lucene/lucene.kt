package uk.dioxic.muon.lucene

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.CharArraySet
import org.apache.lucene.document.*
import org.apache.lucene.index.IndexOptions
import org.apache.lucene.util.BytesRef
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
        searchAnalyser.tokenStream("search", "$artist $title $lyricist"), searchFieldType)
    )
    add(StringField("id", id, Field.Store.YES))
    add(TextField("artist", artist, Field.Store.NO))
    add(TextField("title", title, Field.Store.NO))
    add(TextField("lyricist", lyricist, Field.Store.NO))
    add(TextField("album", album, Field.Store.NO))
    add(TextField("comment", comment, Field.Store.NO))
    add(StringField("genre", genre, Field.Store.NO))
    add(IntPoint("year", year))
    add(StringField("fileType", fileType.toString(), Field.Store.NO))
    add(IntPoint("bitrate", bitrate))
    add(IntPoint("length", length))
    add(StringField("path", path, Field.Store.NO))
    add(StringField("filename", filename, Field.Store.NO))
    add(IntPoint("filesize", fileSize))

    add(SortedDocValuesField("artist_sort", BytesRef(artist)))
    add(SortedDocValuesField("title_sort", BytesRef(title)))
    add(SortedDocValuesField("lyricist_sort", BytesRef(lyricist)))
    add(SortedDocValuesField("album_sort", BytesRef(album)))
}