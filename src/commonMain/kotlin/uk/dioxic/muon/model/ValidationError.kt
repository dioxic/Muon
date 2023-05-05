package uk.dioxic.muon.model

import kotlinx.serialization.Serializable

@Serializable
data class ValidationError(
    val id: String,
    val msg: String
)

typealias ValidationErrors = MutableList<ValidationError>
