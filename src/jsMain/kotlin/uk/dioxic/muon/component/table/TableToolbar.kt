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

external interface EnhancedTableToolbarProps : Props {
    var title: String
    var selected: ReadonlyArray<Row<*>>
    var actions: List<ToolbarAction>
//    var deleteRowHandler:
//    var preGlobalFilteredRows
//    var setGlobalFilter
//    var globalFilter
}

data class ToolbarAction(
    val name: String,
    val size: Size = Size.small,
    val icon: SvgIconComponent,
    val iconColor: IconButtonColor? = null,
    val requiresSelection: Boolean = false,
    val onClick: (ReadonlyArray<Row<*>>) -> Unit,
)

private operator fun EnhancedTableToolbarProps.component1() = title
private operator fun EnhancedTableToolbarProps.component2() = selected
private operator fun EnhancedTableToolbarProps.component3() = actions

val EnhancedTableToolbar = FC<EnhancedTableToolbarProps> { (title, selected, actions) ->

    val theme = useContext(ThemeContext)

    Toolbar {
        sx {
            if (selected.isNotEmpty()) {
                val colour = kotlinx.css.Color(theme.palette.primary.main.asDynamic().unsafeCast<String>())
                val opacity = theme.palette.action.activatedOpacity.unsafeCast<Double>()
                backgroundColor = colour.withAlpha(opacity).value.unsafeCast<BackgroundColor>()
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