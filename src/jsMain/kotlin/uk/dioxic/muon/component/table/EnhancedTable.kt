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
import uk.dioxic.muon.component.table.actions.ToolbarAction
import uk.dioxic.muon.model.Track

external interface EnhancedTableProps<T: Any> : Props {
    var title: String
    var tableInstance: TableInstance<T>
    var toolbarActions: List<ToolbarAction<T>>
    var selectedRows: ReadonlyArray<Row<T>>
}

// TODO use proper types when wrapper supports it - https://github.com/JetBrains/kotlin-wrappers/issues/1129
val EnhancedTable = FC<EnhancedTableProps<Track>> { props ->

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
                                        minWidth = 121.px
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