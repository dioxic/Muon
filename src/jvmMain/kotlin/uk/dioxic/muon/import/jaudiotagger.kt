package uk.dioxic.muon.import

import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.SupportedFileFormat
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import uk.dioxic.muon.audio.AudioFile
import uk.dioxic.muon.model.FileType
import uk.dioxic.muon.model.Track
import uk.dioxic.muon.nullIfBlank
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
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

fun AudioFile.getPath(): Path = Path(this.location.path).resolve(this.location.filename)

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

private fun getArtistAndTitleFromFilename(filename: String): Pair<String, String> {
    val regex = Regex("(.*)-(.*)")
    regex.find(filename)?.let {
        if (it.groupValues.size >= 3) {
            return it.groupValues[1].trim() to it.groupValues[2].trim()
        }
    }
    return Pair("", "")
}

fun File.toTrack(): Track {
    require(this.isAudioFile)
    val audioFile = AudioFileIO.read(this)
    val (artist, title) = getArtistAndTitleFromFilename(this.nameWithoutExtension)

    val tag = audioFile.tagOrCreateDefault

    return Track(
        id = UUID.randomUUID().toString(),
        album = tag.album,
        artist = tag.artist.nullIfBlank() ?: artist,
        bitrate = audioFile.audioHeader.bitRateAsNumber.toInt(),
        comment = tag.comment,
        fileSize = Files.size(this.toPath()).toInt(),
        fileType = this.toFileType(),
        filename = this.nameWithoutExtension,
        genre = tag.genre,
        length = audioFile.audioHeader.trackLength,
        lyricist = tag.lyricist,
        path = this.parent,
        title = tag.title.nullIfBlank() ?: title,
        year = tag.year,
    )
}

fun File.toFileType() = when (this.extension.lowercase()) {
    "mp3" -> FileType.MP3
    "flac" -> FileType.FLAC
    "wav" -> FileType.WAV
    else -> FileType.UNKNOWN
}

