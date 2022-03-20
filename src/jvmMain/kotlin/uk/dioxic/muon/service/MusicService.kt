package uk.dioxic.muon.service

import uk.dioxic.muon.audio.AudioDetails
import uk.dioxic.muon.repository.MusicRepository

interface MusicService : MusicRepository {

    fun attachDuplicates(audioList: List<AudioDetails>): List<AudioDetails>

    fun search(
        libraryId: String? = null,
        text: String? = null,
        searchFields: Array<String> = arrayOf("artist", "title", "lyricist"),
        maxResults: Int,
        after: Int? = null,
        sortField: String? = null,
        sortReverse: Boolean = false
    ): List<AudioDetails>

}