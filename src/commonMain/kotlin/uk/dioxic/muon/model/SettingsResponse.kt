package uk.dioxic.muon.model

import kotlinx.serialization.Serializable
import uk.dioxic.muon.config.Settings

@Serializable
data class SettingsResponse(
    val settings: Settings,
    val errors: ValidationErrors
)