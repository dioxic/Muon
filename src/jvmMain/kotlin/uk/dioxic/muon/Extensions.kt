package uk.dioxic.muon

import org.jaudiotagger.audio.SupportedFileFormat
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import java.io.File

val Tag.artists: List<String>
    get() = getFirst(FieldKey.ARTIST).split("/")

val Tag.title: String
    get() = getFirst(FieldKey.TITLE)

val Tag.comment: String
    get() = getFirst(FieldKey.COMMENT)

val Tag.album: String
    get() = getFirst(FieldKey.ALBUM)

val Tag.lyricist: String
    get() = getFirst(FieldKey.LYRICIST)

val Tag.track: String
    get() = getFirst(FieldKey.TRACK)

val Tag.year: String
    get() = getFirst(FieldKey.YEAR)

val Tag.genre: String
    get() = getFirst(FieldKey.GENRE)

val File.isAudioFile: Boolean
    get() = this.isFile && SupportedFileFormat.values().map { it.filesuffix }.contains(this.extension)