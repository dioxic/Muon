package uk.dioxic.muon.model

import kotlinx.serialization.Serializable

@Serializable
data class Library(
    val id: String,
    val name: String,
    val path: String,
    val genre: String?,
)