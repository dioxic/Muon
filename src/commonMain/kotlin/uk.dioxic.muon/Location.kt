package uk.dioxic.muon

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val path: String,
    val filename: String
)