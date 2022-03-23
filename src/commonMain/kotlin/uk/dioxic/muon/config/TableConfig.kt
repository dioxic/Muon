package uk.dioxic.muon.config

import kotlinx.serialization.Serializable

@Serializable
data class TableConfig(
    val columns: List<TableColumnConfig>
)