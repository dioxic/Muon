package uk.dioxic.muon.service

import uk.dioxic.muon.audio.AudioFile
import java.nio.file.Path

interface ImportService {

    fun getImportFiles(): List<AudioFile>

//    fun movefile(audioFile: AudioFile, newPath: Path): AudioFile
//
//    fun saveTags(audioFile: AudioFile)

    fun save(audioFile: AudioFile)

    fun delete(id: String)

}