package uk.dioxic.muon.component.table.columns

import js.core.jso
import mui.icons.material.Circle
import mui.icons.material.CircleOutlined
import mui.material.*
import mui.system.sx
import react.ReactNode
import react.VFC
import react.create
import react.createElement
import react.dom.html.ReactHTML
import tanstack.table.core.CellContext
import tanstack.table.core.ColumnDefTemplate
import tanstack.table.core.HeaderContext
import uk.dioxic.muon.component.table.actions.RowAction
import uk.dioxic.muon.model.Track
import uk.dioxic.muon.model.toHex
import web.cssom.Color
import web.cssom.Display

fun colorCellTemplate(): ColumnDefTemplate<CellContext<Track, Any>> =
    ColumnDefTemplate { ctx ->
        ctx.row.original.color?.let { rbColor ->
            createElement(Circle, jso {
                sx {
                    color = Color(rbColor.toHex())
                }
            })
        } ?: CircleOutlined.create()
    }

fun ratingCellTemplate(): ColumnDefTemplate<CellContext<Track, Any>> =
    ColumnDefTemplate { ctx ->
        createElement(Rating, jso {
            value = ctx.row.original.rating ?: 0
            readOnly = true
        })
    }

fun <TProps : Any> checkboxHeaderTemplate(): ColumnDefTemplate<HeaderContext<TProps, Any>> =
    ColumnDefTemplate { headerCtx ->
        val table = headerCtx.table
        createElement(Checkbox, jso {
            color = CheckboxColor.primary
            indeterminate = table.getIsSomeRowsSelected()
            checked = table.getIsAllRowsSelected()
            onChange = { event, _ -> table.getToggleAllPageRowsSelectedHandler().invoke(event) }
        })
    }

fun <TProps : Any> checkboxCellTemplate(): ColumnDefTemplate<CellContext<TProps, Any>> =
    ColumnDefTemplate { cellCtx ->
        val row = cellCtx.row
        createElement(Checkbox, jso {
            color = CheckboxColor.primary
            disabled = !row.getCanSelect()
            indeterminate = row.getIsSomeSelected()
            checked = row.getIsSelected()
            onChange = { _, checked -> row.toggleSelected(checked) }

        })
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
                    val icon = when {
                        action.iconFn != null -> action.iconFn.invoke(cellCtx.row.original)
                        action.icon != null -> action.icon
                        else -> error("require icon or iconFn attribute not null")
                    }
                    val iconColor = when {
                        action.iconColorFn != null -> action.iconColorFn.invoke(cellCtx.row.original)
                        action.iconColor != null -> action.iconColor
                        else -> null
                    }

                    Tooltip {
                        title = ReactNode(action.name)
                        enterDelay = 3000
                        IconButton {
                            iconColor?.let {
                                color = it
                            }
                            size = Size.small
                            onClick = { event ->
                                event.stopPropagation()
                                action.onClick(cellCtx.row.original)
                            }

                            icon()
                        }
                    }
                }
            }
        }.create()
    }