package uk.dioxic.muon.repository

import kotlinx.coroutines.flow.Flow
import uk.dioxic.muon.model.Track
import uk.dioxic.muon.model.Tracks

interface TrackRepository {

    fun getTrackById(id: String): Track?

    fun getTracksById(ids: List<String>): Flow<Track>

    fun getPathById(id: String) = getTrackById(id)?.path

}