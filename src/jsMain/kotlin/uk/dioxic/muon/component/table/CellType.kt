package uk.dioxic.muon.component.table

import mui.material.TableCellAlign

enum class CellType(val id: String, val cellAlign: TableCellAlign) {
    DEFAULT("default", TableCellAlign.left),
    CHECKBOX("checkbox", TableCellAlign.left),
    ACTION("action", TableCellAlign.center),
    EXPANDER("expander", TableCellAlign.left);

    companion object {
        fun getCellType(id: String) =
            values().find { it.id == id } ?: DEFAULT
    }
}