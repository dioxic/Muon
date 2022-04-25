package uk.dioxic.muon.model

import kotlinx.serialization.Serializable

@Serializable
data class ImportResponse(
    val successes: List<String> = emptyList(),
    val errors: Map<String, String> = emptyMap()
)