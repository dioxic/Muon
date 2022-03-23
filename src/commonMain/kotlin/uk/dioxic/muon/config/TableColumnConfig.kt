package uk.dioxic.muon.config

import kotlinx.serialization.Serializable

@Serializable
data class TableColumnConfig(
    val id: String,
    val label: String,
    val minWidth: Int,
    val align: String = "left",
//    val width: Int,
    val visible: Boolean = true,
)