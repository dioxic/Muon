package uk.dioxic.muon.service

import uk.dioxic.muon.audio.AudioFile
import uk.dioxic.muon.audio.ImportError
import uk.dioxic.muon.config.Library

interface LibraryService {

    fun reload(libraryId: String)

    fun getFiles(libraryId: String): List<AudioFile>

    fun getLibrary(libraryId: String): Library

    fun saveLibrary(library: Library)

    fun deleteLibrary(libraryId: String)

    fun saveFile(audioFile: AudioFile, overwrite: Boolean = false)

    fun saveFiles(audioFiles: List<AudioFile>, overwrite: Boolean = false): List<ImportError>

    fun deleteFile(fileId: String)

}