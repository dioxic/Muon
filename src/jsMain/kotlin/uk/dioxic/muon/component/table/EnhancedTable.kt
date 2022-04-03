package uk.dioxic.muon.component.table

import csstype.MinWidth
import mui.material.*
import mui.system.sx
import react.FC
import react.Props
import react.ReactElement
import react.table.RenderType
import react.table.TableInstance


@Deprecated("not using this anymore")
external interface EnhancedReactTableProps<T: Any> : Props {
    var table: TableInstance<T>
    var toolbar: ReactElement<EnhancedTableToolbarProps>?
//    var title: String
//    var data: Array<T>
//    var columns: ReadonlyArray<Column<T, *>>
//    var toolbarActions: List<ToolbarAction>
    var minWidth: MinWidth
//    var rowActions: List<RowAction>
}

@Deprecated("not using this anymore")
fun <T: Any> enhancedReactTable() = FC<EnhancedReactTableProps<T>> { props ->
//val EnhancedReactTable = FC<EnhancedReactTableProps<Any>> { props ->

    TableContainer {
        props.toolbar?.let {
            child(it)
        }

//        EnhancedTableToolbar {
//            title = props.title
//            actions = props.toolbarActions
//            selected = props.table.selectedFlatRows
//        }
        Table {
            size = Size.small

            +props.table.getTableProps()

            TableHead {
                props.table.headerGroups.forEach { headerGroup ->
                    TableRow {
                        +headerGroup.getHeaderGroupProps()

                        headerGroup.headers.forEach { h ->
                            val originalHeader = h.placeholderOf
                            val header = originalHeader ?: h

                            TableCell {
                                val isCheckboxCell = (header.id == "selection")

                                if (isCheckboxCell) {
                                    +header.getHeaderProps()
                                } else {
                                    sx {
                                        minWidth = props.minWidth
                                    }
                                    +header.getHeaderProps(header.getSortByToggleProps())
                                }

                                +header.render(RenderType.Header)

                                if (!isCheckboxCell) {
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
                props.table.rows.forEach { row ->
                    props.table.prepareRow(row)

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