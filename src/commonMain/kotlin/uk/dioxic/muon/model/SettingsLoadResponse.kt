package uk.dioxic.muon.model

import kotlinx.serialization.Serializable
import uk.dioxic.muon.config.Settings

@Serializable
data class SettingsLoadResponse(
    val settings: Settings,
    val error: String? = null,
)
