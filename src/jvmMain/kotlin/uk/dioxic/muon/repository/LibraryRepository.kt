package uk.dioxic.muon.repository

import uk.dioxic.muon.audio.AudioFile
import uk.dioxic.muon.config.Library
import java.nio.file.Path

interface LibraryRepository {

    fun getFiles(library: Library): List<AudioFile>

    fun saveTags(audioFile: AudioFile)

}