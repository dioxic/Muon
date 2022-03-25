package uk.dioxic.muon.repository

import kotlinx.coroutines.FlowPreview
import org.apache.lucene.search.Query
import uk.dioxic.muon.audio.AudioDetails
import uk.dioxic.muon.audio.AudioFile
import uk.dioxic.muon.model.Library
import kotlin.time.ExperimentalTime


interface MusicRepository {

    fun size(): Int

    fun getById(id: String): AudioFile

    fun update(libraryId: String? = null, audioFile: AudioFile)
    fun updateMany(libraryId: String? = null, audioFiles: List<AudioFile>)

    fun search(
        query: Query,
        maxResults: Int,
        sortField: String,
        sortReverse: Boolean = false
    ): List<AudioDetails>

    fun searchAfter(
        query: Query,
        maxResults: Int,
        after: Int,
        sortField: String,
        sortReverse: Boolean = false
    ): List<AudioDetails>

    fun search(
        query: Query,
        maxResults: Int,
    ): List<AudioDetails>

    fun searchAfter(
        query: Query,
        maxResults: Int,
        after: Int,
    ): List<AudioDetails>

    fun getDuplicates(audioFiles: List<AudioFile>): List<List<AudioDetails>>

    fun deleteById(id: String)

    @FlowPreview
    @ExperimentalTime
    suspend fun refreshIndex(library: Library): Int
}