package uk.dioxic.muon.config

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import uk.dioxic.muon.ConfigKey

@Polymorphic
sealed interface Config {
    fun key(): ConfigKey
}