package uk.dioxic.muon.component.table

import csstype.Position
import csstype.integer
import csstype.number
import csstype.px
import kotlinx.js.ReadonlyArray
import mui.icons.material.SvgIconComponent
import mui.material.*
import mui.material.styles.Theme
import mui.material.styles.useTheme
import mui.system.sx
import react.FC
import react.Props
import react.ReactNode
import react.dom.html.ReactHTML
import react.table.Row
import uk.dioxic.muon.external.chroma

external interface TableToolbarProps : Props {
    var title: String
    var selected: ReadonlyArray<Row<*>>
    var actions: List<ToolbarAction>
}

data class ToolbarAction(
    val name: String,
    val icon: SvgIconComponent,
    val iconColor: IconButtonColor? = null,
    val requiresSelection: Boolean = false,
    val onClick: (ReadonlyArray<Row<*>>) -> Unit,
    val fetchingAnimation: Boolean = false,
)

private operator fun TableToolbarProps.component1() = title
private operator fun TableToolbarProps.component2() = selected
private operator fun TableToolbarProps.component3() = actions

val TableToolbar = FC<TableToolbarProps> { (title, selected, actions) ->

    val theme = useTheme<Theme>()

    Toolbar {
        sx {
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
                Box {
                    sx {
                        position = Position.relative
                    }

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

                    if (action.fetchingAnimation) {
                        CircularProgress {
                            size = 32.px
                            color = CircularProgressColor.success

                            sx {
                                position = Position.absolute
                                top = 4.px
                                left = 4.px
                                zIndex = integer(1)
//                                color = kotlinx.css.Color.green.value.asDynamic().unsafeCast<ColorProperty>()
                            }

                            variant = CircularProgressVariant.indeterminate
                        }
                    }
                }
            }
        }
    }
}