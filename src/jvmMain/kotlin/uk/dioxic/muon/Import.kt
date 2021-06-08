package uk.dioxic.muon

import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.SupportedFileFormat
import java.io.File
import uk.dioxic.muon.audio.AudioFile
import uk.dioxic.muon.audio.Tags
import uk.dioxic.muon.audio.Header
import uk.dioxic.muon.audio.Location

fun readAudioFiles(dir: File): List<AudioFile> =
    if (dir.isFile) {
        sequenceOf(dir)
    } else {
        dir.walk()
    }.filter { it.isAudioFile }
        .map { readAudioFile(it) }
        .toList()

fun readAudioFile(f: File): AudioFile {
    require(f.isAudioFile)
    val audioFile = AudioFileIO.read(f)
    return AudioFile(
        tags = Tags(
            artist = audioFile.tag.artists.first(),
            title = audioFile.tag.title,
            genre = audioFile.tag.genre,
            comment = audioFile.tag.comment,
            year = audioFile.tag.year,
            album = audioFile.tag.album,
            lyricist = audioFile.tag.lyricist,
        ),
        location = Location(
            path = f.absolutePath,
            filename = f.name,
        ),
        header = Header(
            length = audioFile.audioHeader.trackLength,
            bitrate = audioFile.audioHeader.bitRateAsNumber.toInt(),
            vbr = audioFile.audioHeader.isVariableBitRate,
            fileType = SupportedFileFormat.values().first { it.filesuffix == f.extension }.displayName,
        )
    )
}

