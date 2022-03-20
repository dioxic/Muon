package uk.dioxic.muon.audio

import kotlinx.serialization.Serializable

@Serializable
data class AudioFileMatch(
    val audioFile: AudioFile,
    val matchScore: Float
)