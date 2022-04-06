package uk.dioxic.muon.component.table

import csstype.BackgroundColor
import csstype.number
import kotlinx.js.ReadonlyArray
import mui.icons.material.SvgIconComponent
import mui.material.*
import mui.system.sx
import react.FC
import react.Props
import react.ReactNode
import react.dom.html.ReactHTML
import react.table.Row
import react.useContext
import uk.dioxic.muon.context.ThemeContext

external interface TableToolbarProps : Props {
    var title: String
    var selected: ReadonlyArray<Row<*>>
    var actions: List<ToolbarAction>
}

data class ToolbarAction(
    val name: String,
    val size: Size = Size.small,
    val icon: SvgIconComponent,
    val iconColor: IconButtonColor? = null,
    val requiresSelection: Boolean = false,
    val onClick: (ReadonlyArray<Row<*>>) -> Unit,
)

private operator fun TableToolbarProps.component1() = title
private operator fun TableToolbarProps.component2() = selected
private operator fun TableToolbarProps.component3() = actions

val TableToolbar = FC<TableToolbarProps> { (title, selected, actions) ->

    val theme = useContext(ThemeContext)

    Toolbar {
        css {
            if (selected.isNotEmpty()) {
                backgroundColor = chroma(theme.palette.primary.main)
                    .alpha(theme.palette.action.activatedOpacity)
                    .hex()

            }
        }
        Typography {
            sx {
                flexGrow = number(1.0)
            }
            component = ReactHTML.div

            if (selected.isNotEmpty()) {
                variant = "subtitle1"
                +"${selected.size} selected"
            } else {
                variant = "h6"
                +title
            }
        }

        actions.forEach { action ->
            if (!action.requiresSelection || selected.isNotEmpty()) {
                Tooltip {
                    this.title = ReactNode(action.name)

                    IconButton {
                        action.iconColor?.let {
                            color = it
                        }
                        onClick = { _ -> action.onClick(selected) }

                        action.icon()
                    }
                }
            }
        }
    }
}