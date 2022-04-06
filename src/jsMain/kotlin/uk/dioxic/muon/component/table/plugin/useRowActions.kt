package uk.dioxic.muon.component.table.plugin

import csstype.Display
import kotlinx.js.jso
import mui.material.Box
import mui.material.Tooltip
import mui.system.sx
import react.FC
import react.Props
import react.ReactNode
import react.create
import react.dom.html.ReactHTML
import react.table.PluginHook
import react.table.SimpleColumn
import uk.dioxic.muon.component.table.CellType
import uk.dioxic.muon.component.table.RowAction

fun <T: Any> useRowActions(actions: List<RowAction<T>>) = PluginHook<T> {

    val newColumn = jso<SimpleColumn<T, *>> {
        id = CellType.ACTION.id

        headerFunction = { _ ->
            "Actions".unsafeCast<ReactNode>()
        }
        cellFunction = { cell ->
            FC<Props> {
                Box {
                    component = ReactHTML.div
                    sx {
                        display = Display.flex
                    }

                    actions.forEach { action ->
                        Tooltip {
                            title = ReactNode(action.name)
                            IconButton {
                                action.iconColor?.let {
                                    color = it
                                }
                                size = action.size
                                onClick = { event ->
                                    event.stopPropagation()
                                    action.onClick(cell.row)
                                }

                                action.icon()
                            }
                        }
                    }
                }
            }.create()
//            createElement(component, cell.getToggleAllRowsSelectedProps())
        }
    }

    allColumns += { columns, _ ->
        arrayOf(*columns, newColumn)
    }
}