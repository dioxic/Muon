package uk.dioxic.muon.component.table.plugin

import kotlinx.js.jso
import mui.icons.material.KeyboardArrowDown
import mui.icons.material.KeyboardArrowUp
import mui.material.IconButton
import react.FC
import react.Props
import react.create
import react.createElement
import react.dom.html.ReactHTML
import react.table.PluginHook
import react.table.SimpleColumn
import react.table.TableExpandedToggleProps
import uk.dioxic.muon.component.table.CellType

val useExpanderColumn = PluginHook<Any> {
    val newColumn = jso<SimpleColumn<Any, *>> {
        id = CellType.EXPANDER.id
        headerFunction = { header ->
            val component = FC<Props> {
                ReactHTML.span {
                    onClick = { _ -> header.toggleAllRowsExpanded(!header.isAllRowsExpanded) }
                    if (header.isAllRowsExpanded) {
                        KeyboardArrowUp()
                    } else {
                        KeyboardArrowDown()
                    }
                }
            }

            component.create()
        }
        cellFunction = { cell ->
            val component = FC<TableExpandedToggleProps> { props ->
                if (cell.row.canExpand) {
                    IconButton {
                        onClick = { _ -> cell.row.toggleRowExpanded(!cell.row.isExpanded) }
                        if (cell.row.isExpanded) {
                            KeyboardArrowUp()
                        } else {
                            KeyboardArrowDown()
                        }

                        +props
                    }
                }
            }

            createElement(component, cell.row.getToggleRowExpandedProps())
        }
    }
    visibleColumns += { columns, _ ->
        arrayOf(newColumn, *columns)
    }
}