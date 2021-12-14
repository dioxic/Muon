package uk.dioxic.muon.audio

import kotlinx.serialization.Serializable

@Serializable
data class ImportError(val id: String, val filename: String, val reason: String)