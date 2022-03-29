package uk.dioxic.muon

import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.SupportedFileFormat
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import uk.dioxic.muon.audio.AudioFile
import uk.dioxic.muon.audio.Header
import uk.dioxic.muon.audio.Location
import uk.dioxic.muon.audio.Tags
import java.io.File
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

private fun getArtistAndTitleFromFilename(filename: String): Tags {
    val regex = Regex("(.*)-(.*)")
    regex.find(filename)?.let {
        if (it.groupValues.size >= 3) {
            return Tags(
                artist = it.groupValues[1].trim(),
                title = it.groupValues[2].trim()
            )
        }
    }
    return Tags()
}

fun File.toAudioFile(): AudioFile {
    require(this.isAudioFile)
    val audioFile = AudioFileIO.read(this)
    val artistAndTitle = getArtistAndTitleFromFilename(this.nameWithoutExtension)

    val tags = if (audioFile.tag != null) {
        Tags(
            artist = audioFile.tag.artist.ifEmpty { artistAndTitle.artist },
            title = audioFile.tag.title.ifEmpty { artistAndTitle.title },
            genre = audioFile.tag.genre,
            comment = audioFile.tag.comment,
            year = audioFile.tag.year,
            album = audioFile.tag.album,
            lyricist = audioFile.tag.lyricist,
        )
    } else {
        artistAndTitle
    }

    return AudioFile(
        id = UUID.randomUUID().toString(),
        tags = tags,
        location = Location(
            path = this.parent,
            filename = this.name,
            extension = this.extension
        ),
        header = Header(
            length = audioFile.audioHeader.trackLength,
            bitrate = audioFile.audioHeader.bitRateAsNumber.toInt(),
            vbr = audioFile.audioHeader.isVariableBitRate,
            fileType = SupportedFileFormat.values().first { it.filesuffix == this.extension }.displayName,
        )
    )
}