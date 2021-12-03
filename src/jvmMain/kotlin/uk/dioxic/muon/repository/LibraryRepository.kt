package uk.dioxic.muon.repository

import uk.dioxic.muon.audio.AudioFile
import java.nio.file.Path

interface LibraryRepository {

    fun getImportFiles(): List<AudioFile>

    fun saveTags(audioFile: AudioFile)

}