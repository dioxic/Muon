package uk.dioxic.muon.repository

import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.SupportedFileFormat
import uk.dioxic.muon.*
import uk.dioxic.muon.audio.AudioFile
import uk.dioxic.muon.audio.Header
import uk.dioxic.muon.audio.Location
import uk.dioxic.muon.audio.Tags
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.name

class LibraryRepositoryImpl(private val configRepository: ConfigRepository) : LibraryRepository {

    override fun getImportFiles(): List<AudioFile> {
        val dir = File(configRepository.getLibraryConfig().importPath)
        require(dir.isDirectory) { "${dir.name} is not a directory!" }

        return dir.walk()
            .filter { it.isAudioFile }
            .map { readAudioFile(it) }
            .toList()
    }

    override fun saveTags(audioFile: AudioFile) {
        AudioFileIO.read(audioFile.getPath().toFile())?.apply {
            merge(audioFile)
            commit()
        }
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

    private fun readAudioFile(f: File): AudioFile {
        require(f.isAudioFile)
        val audioFile = AudioFileIO.read(f)
        val artistAndTitle = getArtistAndTitleFromFilename(f.nameWithoutExtension)

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
                path = f.parent,
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

}