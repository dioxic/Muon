package uk.dioxic.muon.component.table

import mui.material.*
import mui.system.sx
import react.FC
import react.Props
import tanstack.react.table.renderCell
import tanstack.react.table.renderHeader
import tanstack.table.core.RowData
import tanstack.table.core.Table
import uk.dioxic.muon.common.getIsSortedBoolean
import uk.dioxic.muon.common.getTableSortLabelDirection
import web.cssom.Cursor

external interface BasicTableProps : Props {
    var table: Table<*>
    var sortable: Boolean
    var selectable: Boolean
}

// TODO use proper types when wrapper supports it - https://github.com/JetBrains/kotlin-wrappers/issues/1129
private operator fun BasicTableProps.component1() = table
private operator fun BasicTableProps.component2() = sortable
private operator fun BasicTableProps.component3() = selectable

val BasicTable = FC<BasicTableProps> { (table, sortable, selectable) ->
    Table {
        size = Size.small
        stickyHeader = true

        TableHead {
            table.getHeaderGroups().forEach { headerGroup ->
                TableRow {
                    headerGroup.headers.forEach { header ->
                        TableCell {
                            if (!header.isPlaceholder) {
                                if (sortable && header.column.getCanSort()) {
                                    sx {
                                        cursor = Cursor.pointer
                                    }
                                    TableSortLabel {
                                        active = header.column.getIsSortedBoolean()
                                        direction = header.column.getTableSortLabelDirection()
                                        onClick = header.column.getToggleSortingHandler()

                                        +renderHeader(header)
                                    }
                                } else {
                                    +renderHeader(header)
                                }
                            }
                        }
                    }
                }
            }
        }

        TableBody {
            table.getSortedRowModel().rows.forEach { row ->
                TableRow {
                    if (selectable) {
                        onClick = { event ->
                            if (row.getCanSelect()) {
                                row.getToggleSelectedHandler()(event)
                            }
                        }
                    }
                    row.getVisibleCells().forEach { cell ->
                        TableCell {
                            +renderCell(cell)
                        }
                    }
                }
            }
        }
    }
}