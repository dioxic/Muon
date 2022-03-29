package uk.dioxic.muon.config

import kotlinx.serialization.Serializable

@Serializable
data class ColumnDefinition(
    val id: String,
    val label: String,
    val minWidth: Int,
    val align: String = "left",
    val visible: Boolean = true,
)