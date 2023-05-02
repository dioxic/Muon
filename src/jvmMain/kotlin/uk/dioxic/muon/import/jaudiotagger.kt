package uk.dioxic.muon.import

import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.SupportedFileFormat
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import uk.dioxic.muon.model.FileType
import uk.dioxic.muon.model.Track
import java.io.File
import java.nio.file.Files
import java.util.*

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

private fun getArtistAndTitleFromFilename(filename: String): Pair<String, String> {
    val regex = Regex("(.*)-(.*)")
    regex.find(filename)?.let {
        if (it.groupValues.size >= 3) {
            return it.groupValues[1].trim() to it.groupValues[2].trim()
        }
    }
    return Pair("", "")
}

fun File.updateTags(track: Track) {
    require(this.exists()) { "File must exist!" }
    require(this.isAudioFile) { "Require an audio file!" }
    val audioFile = AudioFileIO.read(this)

    audioFile.tagOrCreateDefault.apply {
        album = track.album
        artist = track.artist
        title = track.title
        comment = track.comment
        genre = track.genre
        lyricist = track.lyricist
        year = track.year
    }

    audioFile.commit()
}

fun File.toTrack(): Track {
    require(this.exists()) { "File must exist!" }
    require(this.isAudioFile) { "Require an audio file!" }
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
        type = this.toFileType(),
        filename = this.nameWithoutExtension,
        genre = tag.genre,
        length = audioFile.audioHeader.trackLength,
        lyricist = tag.lyricist,
        path = this.absolutePath,
        title = tag.title.nullIfBlank() ?: title,
        year = tag.year,
    )
}

fun File.toFileType() = when (this.extension.lowercase()) {
    "mp3" -> FileType.MP3
    "flac" -> FileType.FLAC
    "wav" -> FileType.WAV
    "aiff" -> FileType.AIFF
    else -> FileType.UNKNOWN
}

