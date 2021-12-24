package uk.dioxic.muon.repository

import uk.dioxic.muon.audio.AudioFile
import uk.dioxic.muon.model.Library

interface MusicRepository {
    fun size(): Int

    fun getById(id: String): AudioFile

    fun update(libraryId: String? = null, audioFile: AudioFile)
    fun updateMany(libraryId: String? = null, audioFiles: List<AudioFile>)

    fun search(
        libraryId: String? = null,
        text: String? = null,
        maxResults: Int,
        fields: Array<String> = arrayOf("artist", "title", "lyricist")
    ): List<AudioFile>

    fun deleteById(id: String)

    suspend fun refreshIndex(library: Library): Int
}