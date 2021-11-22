package uk.dioxic.muon

import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.SupportedFileFormat
import uk.dioxic.muon.audio.AudioFile
import uk.dioxic.muon.audio.Header
import uk.dioxic.muon.audio.Location
import uk.dioxic.muon.audio.Tags
import java.io.File

fun readAudioFiles(dir: File): List<AudioFile> =
    if (dir.isFile) {
        sequenceOf(dir)
    } else {
        dir.walk()
    }.filter { it.isAudioFile }
        .map { readAudioFile(it) }
        .toList()

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

fun readAudioFile(f: File): AudioFile {
    require(f.isAudioFile)
    val audioFile = AudioFileIO.read(f)
    val artistAndTitle = getArtistAndTitleFromFilename(f.nameWithoutExtension)

    val tags = if (audioFile.tag != null) {
        Tags(
            artist = audioFile.tag.artists.first().ifEmpty { artistAndTitle.artist },
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
        tags = tags,
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

