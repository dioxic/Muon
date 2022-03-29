package uk.dioxic.muon.model

import csstype.Length
import csstype.px
import kotlinx.js.jso
import mui.material.TableCellAlign
import uk.dioxic.muon.config.ColumnDefinition

private fun String.toTableCellAlign() =
    when (this) {
        "left" -> TableCellAlign.left
        "center" -> TableCellAlign.center
        "right" -> TableCellAlign.right
        "justify" -> TableCellAlign.justify
        else -> TableCellAlign.left
    }

external interface TableColumn {
    var id: String
    var label: String
    var minWidth: Length
    var align: TableCellAlign
    var visible: Boolean
}

fun Collection<ColumnDefinition>.toColumns() =
    this.map { it.toColumn() }

fun ColumnDefinition.toColumn(): TableColumn =
    jso {
        this.id = this@toColumn.id
        this.label = this@toColumn.label
        this.minWidth = this@toColumn.minWidth.px
        this.align = this@toColumn.align.toTableCellAlign()
        this.visible = this@toColumn.visible
    }
