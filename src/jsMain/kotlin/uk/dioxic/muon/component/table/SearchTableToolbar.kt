package uk.dioxic.muon.component.table

import mui.icons.material.Search
import mui.material.*
import mui.material.styles.Theme
import mui.material.styles.TypographyVariant
import mui.material.styles.useTheme
import mui.system.sx
import org.w3c.dom.HTMLInputElement
import react.FC
import react.Props
import react.StateInstance
import react.StateSetter
import react.dom.html.ReactHTML
import uk.dioxic.muon.common.debounce
import uk.dioxic.muon.component.table.actions.ToolbarAction
import uk.dioxic.muon.external.chroma
import web.cssom.*

external interface SearchTableToolbarProps : Props {
    var title: String
    var searchText: String
    var setSearchText: StateSetter<String>
    var isFetching: Boolean
}

// TODO use proper types when wrapper supports it - https://github.com/JetBrains/kotlin-wrappers/issues/1129
private operator fun SearchTableToolbarProps.component1() = title
private operator fun SearchTableToolbarProps.component2() = searchText
private operator fun SearchTableToolbarProps.component3() = setSearchText
private operator fun SearchTableToolbarProps.component4() = isFetching

val SearchTableToolbar = FC<SearchTableToolbarProps> { (title, searchText, setSearchText, isFetching) ->
    val theme = useTheme<Theme>()

    Toolbar {
        Typography {
            sx {
                flexGrow = number(1.0)
            }
            component = ReactHTML.div
            variant = TypographyVariant.h6
            +title
        }
        Paper {
            sx {
                display = Display.flex
                alignItems = AlignItems.center
                width = 400.px
                backgroundColor = chroma(theme.palette.primary.main)
                    .alpha(theme.palette.action.activatedOpacity)
                    .hex()
            }

            InputBase {
                sx {
                    flex = Flex.fitContent
                    marginLeft = 1.em
                }
                spellCheck = false
                autoComplete = "off"
                placeholder = "Search Library"
                onChange = { event ->
                    debounce((event.target.asDynamic() as HTMLInputElement).value, 500) {
                        if (it != searchText && (it.length > 3 || it.isEmpty())) {
                            setSearchText(it)
                        }
                    }
                }
            }
            Box {
                sx {
                    position = Position.relative
                    padding = 8.px
                    MuiSvgIcon.root {
                        verticalAlign = VerticalAlign.middle
                    }
                }
                Search()

                if (isFetching) {
                    CircularProgress {
                        size = 36.px
                        color = CircularProgressColor.success

                        sx {
                            position = Position.absolute
                            top = 2.px
                            left = 2.px
                            zIndex = integer(1)
                        }

                        variant = CircularProgressVariant.indeterminate
                    }
                }
            }
        }
    }
}