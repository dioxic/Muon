package uk.dioxic.muon.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import org.apache.logging.log4j.kotlin.logger
import uk.dioxic.muon.import.isAudioFile
import uk.dioxic.muon.import.toTrack
import uk.dioxic.muon.model.Track
import java.io.File

class ImportRepository(private val settingsRepository: SettingsRepository) : TrackRepository {

    private val logger = logger()
    private val trackById = mutableMapOf<String, Track>()
    private val trackByPath = mutableMapOf<String, Track>()

    fun getTracks(): List<Track> {
        val dirs = settingsRepository.get().downloadDirs
        require(dirs.isNotEmpty()) { "download directory not set!" }

        return dirs.map { File(it) }
            .flatMap(this::getTracks)
    }

    private fun getTracks(dir: File): List<Track> {
        require(dir.isDirectory) { "${dir.absolutePath} is not a directory!" }

        logger.debug("Reading files from ${dir.absolutePath}...")

        return dir.walk()
            .filter { it.isAudioFile }
            .map(this::toTrack)
            .map(this::cacheAndUpdate)
            .toList()
    }

    private fun getTrack(f: File): Track {
        require(f.isAudioFile) { "${f.name} is not an audio file!" }

        return cacheAndUpdate(toTrack(f))
    }

    private fun cacheAndUpdate(track: Track): Track {
        val existingId = trackByPath[track.path]?.id
        val newTrack = existingId?.let { track.copy(id = existingId) } ?: track
        trackByPath[track.path] = newTrack
        trackById[track.id] = newTrack
        return newTrack
    }

    override fun getTrackById(id: String): Track? {
        return trackById[id]
    }

    override fun getTracksById(ids: List<String>) = ids.asFlow()
        .mapNotNull { id -> trackById[id] }

    private fun toTrack(f: File) =
        try {
            f.toTrack()
        } catch (e: Throwable) {
            logger.error("error on ${f.name}")
            throw e
        }


}