package uk.dioxic.muon.model

import kotlinx.serialization.Serializable

@Serializable
data class SettingsSaveResponse(
    val error: String? = null
)
