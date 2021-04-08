package uk.dioxic.muon

import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.SupportedFileFormat
import java.io.File

fun readMusicFiles(dir: File): List<MusicFile> =
    if (dir.isFile) {
        sequenceOf(dir)
    } else {
        dir.walk()
    }.filter { it.isAudioFile }
        .map { readMusicFile(it) }
        .toList()

fun readMusicFile(f: File): MusicFile {
    require(f.isAudioFile)
    val audioFile = AudioFileIO.read(f)
    return MusicFile(
        path = f.absolutePath,
        filename = f.name,
        artist = audioFile.tag.artists.first(),
        title = audioFile.tag.title,
        genre = audioFile.tag.genre,
        comment = audioFile.tag.comment,
        length = audioFile.audioHeader.trackLength,
        bitrate = audioFile.audioHeader.bitRateAsNumber.toInt(),
        vbr = audioFile.audioHeader.isVariableBitRate,
        fileType = SupportedFileFormat.values().first { it.filesuffix == f.extension }.displayName,
    )
}

