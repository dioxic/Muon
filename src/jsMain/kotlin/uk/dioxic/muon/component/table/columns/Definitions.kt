package uk.dioxic.muon.component.table.columns

import mui.material.*
import mui.system.sx
import react.ReactNode
import react.VFC
import react.create
import react.dom.html.ReactHTML
import tanstack.table.core.CellContext
import tanstack.table.core.ColumnDefTemplate
import tanstack.table.core.HeaderContext
import uk.dioxic.muon.component.table.actions.RowAction
import web.cssom.Display

fun <TProps : Any> checkboxHeaderTemplate(): ColumnDefTemplate<HeaderContext<TProps, Any>> =
    ColumnDefTemplate { headerCtx ->
        VFC {
            val table = headerCtx.table
            Checkbox {
                color = CheckboxColor.primary
                indeterminate = table.getIsSomeRowsSelected()
                checked = table.getIsAllRowsSelected()
                onChange = { event, _ -> table.getToggleAllPageRowsSelectedHandler().invoke(event) }
            }
        }.create()
    }

fun <TProps : Any> checkboxCellTemplate(): ColumnDefTemplate<CellContext<TProps, Any>> =
    ColumnDefTemplate { cellCtx ->
        VFC {
            val row = cellCtx.row
            Checkbox {
                color = CheckboxColor.primary
                disabled = !row.getCanSelect()
                indeterminate = row.getIsSomeSelected()
                checked = row.getIsSelected()
                onChange = { _, checked -> row.toggleSelected(checked) }
            }
        }.create()
    }

fun <TProps : Any> rowActionTemplate(actions: List<RowAction<TProps>>): ColumnDefTemplate<CellContext<TProps, Any>> =
    ColumnDefTemplate { cellCtx ->
        VFC {
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
                            size = Size.small
                            onClick = { event ->
                                event.stopPropagation()
                                action.onClick(cellCtx.row.original)
                            }

                            action.icon()
                        }
                    }
                }
            }
        }.create()
    }