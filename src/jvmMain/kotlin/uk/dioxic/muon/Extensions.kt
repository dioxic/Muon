package uk.dioxic.muon

import org.jaudiotagger.audio.SupportedFileFormat
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import uk.dioxic.muon.audio.AudioFile
import uk.dioxic.muon.audio.Location
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path

var Tag.artist: String
    get() = getFirst(FieldKey.ARTIST) //.split("/")
    set(value) = setField(FieldKey.ARTIST, value)

var Tag.title: String
    get() = getFirst(FieldKey.TITLE)
    set(value) = setField(FieldKey.TITLE, value)

var Tag.comment: String
    get() = getFirst(FieldKey.COMMENT)
    set(value) = setField(FieldKey.COMMENT, value)

var Tag.album: String
    get() = getFirst(FieldKey.ALBUM)
    set(value) = setField(FieldKey.ALBUM, value)

var Tag.lyricist: String
    get() = getFirst(FieldKey.LYRICIST)
    set(value) = setField(FieldKey.LYRICIST, value)

var Tag.track: String
    get() = getFirst(FieldKey.TRACK)
    set(value) = setOrRemoveField(FieldKey.TRACK, value)

var Tag.year: String
    get() = getFirst(FieldKey.YEAR)
    set(value) = setOrRemoveField(FieldKey.YEAR, value)

var Tag.genre: String
    get() = getFirst(FieldKey.GENRE)
    set(value) = setField(FieldKey.GENRE, value)

private fun Tag.setOrRemoveField(fieldKey: FieldKey, value: String) {
    if (value.isEmpty()) {
        deleteField(fieldKey)
    } else {
        setField(fieldKey, value)
    }
}

val File.isAudioFile: Boolean
    get() = this.isFile && SupportedFileFormat.values().map { it.filesuffix }.contains(this.extension)

fun AudioFile.getPath(): Path = this.location.getPath()

fun Location.getPath(): Path = Path(this.path).resolve(this.filename)

fun org.jaudiotagger.audio.AudioFile.merge(audioFile: AudioFile) {
    if (this.tag == null) {
        this.tag = this.createDefaultTag()
    }

    this.tag.artist = audioFile.tags.artist
    this.tag.title = audioFile.tags.title
    this.tag.comment = audioFile.tags.comment
    this.tag.album = audioFile.tags.album
    this.tag.lyricist = audioFile.tags.lyricist
    this.tag.genre = audioFile.tags.genre
    this.tag.year = audioFile.tags.year
}