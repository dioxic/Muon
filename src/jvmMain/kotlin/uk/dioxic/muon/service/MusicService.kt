package uk.dioxic.muon.service

import kotlinx.coroutines.flow.toList
import kotlinx.datetime.Clock
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

    suspend fun search(text: String, maxResults: Int): List<Track> {
        val trackIds = luceneRepository.search(text, maxResults)
        val tracks = rekordboxRepository.getRekordboxTracksById(trackIds)
        return tracks.toList(mutableListOf())
    }

    suspend fun buildIndex() =
        luceneRepository.upsert(
            rekordboxRepository.getRekordboxTracks()
        )

    suspend fun refreshIndex(): Int {
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val count = luceneRepository.upsert(
            rekordboxRepository.getRekordboxTracks(settingsRepository.get().lastRekordboxRefresh)
        )
        settingsRepository.save(settingsRepository.get().copy(lastRekordboxRefresh = now))
        return count
    }

}