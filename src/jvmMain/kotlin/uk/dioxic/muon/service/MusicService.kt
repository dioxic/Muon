package uk.dioxic.muon.service

import kotlinx.coroutines.flow.toList
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import uk.dioxic.muon.model.Track
import uk.dioxic.muon.repository.LuceneRepository
import uk.dioxic.muon.repository.RekordboxRepository
import uk.dioxic.muon.repository.SettingsRepository

class MusicService(
    private val luceneRepository: LuceneRepository,
    private val rekordboxRepository: RekordboxRepository,
    private val settingsRepository: SettingsRepository
) {

    suspend fun search(text: String?, maxResults: Int): List<Track> {
        val trackIds = luceneRepository.search(text, maxResults)
        return rekordboxRepository.getTracksById(trackIds)
            .toList(mutableListOf())
//            .map { track ->
//                val score = trackIds
//                    .find { tas -> tas.first == track.id }
//                    ?.second
//
//                score?.let {
//                    track.copy(score = it)
//                } ?: track
//            }
//        return tracks.toList(mutableListOf())
    }

    suspend fun rebuildIndex() = buildIndex()

    suspend fun refreshIndex() = buildIndex(settingsRepository.get().lastRekordboxRefresh)

    private suspend fun buildIndex(lastRefresh: LocalDateTime? = null): Int {
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val count = luceneRepository.upsert(
            rekordboxRepository.getTracks(lastRefresh)
        )
        settingsRepository.save(settingsRepository.get().copy(lastRekordboxRefresh = now))
        return count
    }

}