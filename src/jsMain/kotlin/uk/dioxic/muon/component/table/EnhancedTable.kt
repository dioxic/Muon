package uk.dioxic.muon.component.table

import csstype.px
import kotlinx.js.ReadonlyArray
import mui.material.*
import mui.system.sx
import react.FC
import react.Props
import react.table.RenderType
import react.table.Row
import react.table.TableInstance

external interface EnhancedTableProps : Props {
    var title: String
    var tableInstance: TableInstance<Any>
    var toolbarActions: List<ToolbarAction>
    var selectedRows: ReadonlyArray<Row<Any>>
}

val EnhancedTable = FC<EnhancedTableProps> { props ->

    TableContainer {
        TableToolbar {
            title = props.title
            actions = props.toolbarActions
            selected = props.selectedRows
        }

        Table {
            size = Size.small

            +props.tableInstance.getTableProps()

            TableHead {
                props.tableInstance.headerGroups.forEach { headerGroup ->
                    TableRow {
                        +headerGroup.getHeaderGroupProps()

                        headerGroup.headers.forEach { h ->
                            val originalHeader = h.placeholderOf
                            val header = originalHeader ?: h

                            TableCell {
                                val cellType = CellType.getCellType(header.id)

                                align = cellType.cellAlign

                                if (cellType == CellType.DEFAULT) {
                                    sx {
                                        minWidth = 120.px
                                    }
                                    +header.getHeaderProps(header.getSortByToggleProps())
                                } else {
                                    +header.getHeaderProps()
                                }

                                +header.render(RenderType.Header)

                                if (cellType == CellType.DEFAULT) {
                                    TableSortLabel {
                                        active = header.isSorted
                                        direction = if (header.isSortedDesc)
                                            TableSortLabelDirection.desc
                                        else
                                            TableSortLabelDirection.asc
                                    }
                                }
                            }
                        }
                    }
                }
            }

            TableBody {

                props.tableInstance.rows.forEach { row ->
                    props.tableInstance.prepareRow(row)
                    TableRow {
                        +row.getRowProps()

                        row.cells.forEach { cell ->
                            TableCell {

                                +cell.getCellProps()
                                +cell.render(RenderType.Cell)
                            }
                        }
                    }
                }
            }
        }
    }
}