package uk.dioxic.muon.model

import kotlinx.serialization.Serializable

@Serializable
data class ImportError(val id: String, val filename: String, val reason: String)