package uk.dioxic.muon.component.table

import csstype.Position
import csstype.integer
import csstype.number
import csstype.px
import kotlinx.js.ReadonlyArray
import mui.material.*
import mui.material.styles.Theme
import mui.material.styles.TypographyVariant
import mui.material.styles.useTheme
import mui.system.sx
import react.FC
import react.Props
import react.ReactNode
import react.dom.html.ReactHTML
import react.table.Row
import uk.dioxic.muon.component.table.actions.ToolbarAction
import uk.dioxic.muon.external.chroma
import uk.dioxic.muon.model.Track

external interface TableToolbarProps<T: Any> : Props {
    var title: String
    var selected: ReadonlyArray<Row<T>>
    var actions: List<ToolbarAction<T>>
}

// TODO use proper types when wrapper supports it - https://github.com/JetBrains/kotlin-wrappers/issues/1129
private operator fun TableToolbarProps<Track>.component1() = title
private operator fun TableToolbarProps<Track>.component2() = selected
private operator fun TableToolbarProps<Track>.component3() = actions

val TableToolbar = FC<TableToolbarProps<Track>> { (title, selected, actions) ->

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
                variant = TypographyVariant.subtitle1
                +"${selected.size} selected"
            } else {
                variant = TypographyVariant.h6
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
                            onClick = { _ -> action.onClick(selected.map { it.original }) }

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
                            }

                            variant = CircularProgressVariant.indeterminate
                        }
                    }
                }
            }
        }
    }
}