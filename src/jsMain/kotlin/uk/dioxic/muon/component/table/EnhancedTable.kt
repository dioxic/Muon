package uk.dioxic.muon.component.table

import csstype.px
import kotlinx.js.ReadonlyArray
import mui.icons.material.KeyboardArrowDown
import mui.icons.material.KeyboardArrowUp
import mui.material.*
import mui.material.styles.Theme
import mui.material.styles.useTheme
import mui.system.sx
import react.FC
import react.Props
import react.ReactNode
import react.table.RenderType
import react.table.Row
import react.table.TableInstance
import react.useState
import uk.dioxic.muon.component.table.actions.ToolbarAction
import uk.dioxic.muon.external.chroma
import uk.dioxic.muon.model.Track

external interface EnhancedTableProps<T : Any> : Props {
    var title: String
    var tableInstance: TableInstance<T>
    var toolbarActions: List<ToolbarAction<T>>
    var selectedRows: ReadonlyArray<Row<T>>
    var columnCount: Int
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
            stickyHeader = true
            size = Size.small

            +props.tableInstance.getTableProps()

            TableHead {
                props.tableInstance.headerGroups.forEach { headerGroup ->
                    TableRow {
                        +headerGroup.getHeaderGroupProps()

                        // for expanding icon
                        TableCell {
                            sx {
                                paddingLeft = 1.px
                                paddingRight = 1.px
                            }
                        }

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
                                    if (cellType != CellType.DEFAULT) {
                                        sx {
                                            paddingLeft = 1.px
                                            paddingRight = 1.px
                                        }
                                    }
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
                    CollapsibleRow {
                        this.row = row
                        columnCount = props.columnCount
                    }
                }
            }
        }
    }
}

external interface CollapsibleRowProps : Props {
    var row: Row<Track>
    var columnCount: Int
}

val CollapsibleRow = FC<CollapsibleRowProps> { props ->
    val theme = useTheme<Theme>()
    val (open, setOpen) = useState(false)
    val hasDuplicates = !props.row.original.duplicates.isNullOrEmpty()

    TableRow {
        if (hasDuplicates) {
            sx {
                MuiTableCell.root {
                    borderBottom = 0.px
                }
            }
            Tooltip {
                title = ReactNode("duplicates")
                TableCell {
                    sx {
                        paddingLeft = 1.px
                        paddingRight = 1.px
                    }

                    IconButton {
                        size = Size.small
                        onClick = { _ -> setOpen(!open) }

                        if (open) {
                            KeyboardArrowUp()
                        } else {
                            KeyboardArrowDown()
                        }
                    }
                }
            }
        } else {
            TableCell {}
        }

        props.row.cells.forEach { cell ->
            TableCell {
                val cellType = CellType.getCellType(cell.column.id)
                if (cellType != CellType.DEFAULT) {
                    sx {
                        paddingLeft = 1.px
                        paddingRight = 1.px
                    }
                }

                +cell.getCellProps()
                +cell.render(RenderType.Cell)
            }
        }

        +props.row.getRowProps()
    }

    if (hasDuplicates) {
        TableRow {
            TableCell {
                sx {
                    paddingBottom = 0.px
                    paddingTop = 0.px
                    paddingLeft = 1.px
                    paddingRight = 1.px
                    backgroundColor = chroma(theme.palette.primary.main)
                        .alpha(theme.palette.action.activatedOpacity)
                        .hex()
                }

                colSpan = props.columnCount
                Collapse {
                    `in` = open
                    Box {
                        sx {
                            margin = 8.px
                        }
                        DuplicatesTable {
                            track = props.row.original
                        }
                    }
                }
            }
        }
    }
}