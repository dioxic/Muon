package uk.dioxic.muon.component.table

import mui.material.*
import mui.material.styles.Theme
import mui.material.styles.TypographyVariant
import mui.material.styles.useTheme
import mui.system.sx
import react.FC
import react.Props
import react.ReactNode
import react.dom.html.ReactHTML
import uk.dioxic.muon.component.table.actions.ToolbarAction
import uk.dioxic.muon.external.chroma
import web.cssom.Position
import web.cssom.integer
import web.cssom.number
import web.cssom.px

external interface ActionTableToolbarProps : Props {
    var title: String
    var selectedCount: Int
    var actions: List<ToolbarAction>
}

// TODO use proper types when wrapper supports it - https://github.com/JetBrains/kotlin-wrappers/issues/1129
private operator fun ActionTableToolbarProps.component1() = title
private operator fun ActionTableToolbarProps.component2() = selectedCount
private operator fun ActionTableToolbarProps.component3() = actions

val ActionTableToolbar = FC<ActionTableToolbarProps> { (title, selectedCount, actions) ->

    val theme = useTheme<Theme>()

    Toolbar {
        sx {
            if (selectedCount > 0) {
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

            if (selectedCount > 0) {
                variant = TypographyVariant.subtitle1
                +"$selectedCount selected"
            } else {
                variant = TypographyVariant.h6
                +title
            }
        }

        actions.forEach { action ->
            if (action.visible) {
                Box {
                    sx {
                        position = Position.relative
                    }

                    Tooltip {
                        this.title = ReactNode(action.name)
                        Box {
                            component = ReactHTML.span

                            IconButton {
                                disabled = action.fetchingAnimation
                                action.iconColor?.let {
                                    color = it
                                }
                                onClick = { _ -> action.onClick() }

                                action.icon()
                            }
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
                            }

                            variant = CircularProgressVariant.indeterminate
                        }
                    }
                }
            }
        }
    }
}