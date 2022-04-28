package uk.dioxic.muon.component

import csstype.Position
import csstype.integer
import csstype.number
import csstype.px
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import mui.icons.material.*
import mui.material.*
import mui.material.styles.Theme
import mui.material.styles.TypographyVariant
import mui.material.styles.useTheme
import mui.system.sx
import react.*
import react.dom.aria.AriaHasPopup.`false`
import react.dom.aria.ariaHasPopup
import react.dom.aria.ariaLabel
import react.dom.html.ReactHTML
import react.router.useLocation
import uk.dioxic.muon.Routes
import uk.dioxic.muon.api.Api
import uk.dioxic.muon.common.Area
import uk.dioxic.muon.common.Themes
import uk.dioxic.muon.context.AlertContext
import uk.dioxic.muon.context.ThemeContext

val Header = FC<Props> {
    val theme = useTheme<Theme>()
    val (_, addAlert) = useContext(AlertContext)
    val toggleColorMode = useContext(ThemeContext)
    val lastPathname = useLocation().pathname.substringAfterLast("/")
    val (indexing, setIndexing) = useState(false)

    AppBar {
        position = AppBarPosition.fixed
        sx {
            gridArea = Area.Header
            zIndex = integer(1_500)
        }

        Toolbar {
            Typography {
                sx { flexGrow = number(1.0) }
                variant = TypographyVariant.h6
                noWrap = true
                component = ReactHTML.div

                +"Music Organisation"
            }

            Tooltip {
                title = ReactNode("Theme")

                Switch {
                    icon = Brightness7.create()
                    checkedIcon = Brightness4.create()
                    checked = theme == Themes.Dark
                    ariaLabel = "theme"

                    onChange = { _, _ -> toggleColorMode() }
                }
            }

            Tooltip {
                title = ReactNode("Read Documentation")

                IconButton {
                    ariaLabel = "official documentation"
                    ariaHasPopup = `false`
                    size = Size.large
                    color = IconButtonColor.inherit
                    onClick = { window.location.href = "https://mui.com/components/$lastPathname" }

                    MenuBook()
                }
            }

            Tooltip {
                title = ReactNode("View Sources")

                IconButton {
                    ariaLabel = "source code"
                    ariaHasPopup = `false`
                    size = Size.large
                    color = IconButtonColor.inherit
                    onClick = {
                        var name = lastPathname
                            .split("-")
                            .asSequence()
                            .map { it.replaceFirstChar { it.titlecase() } }
                            .reduce { accumulator, word -> accumulator.plus(word) }

                        if (name.isNotEmpty()) {
                            name += ".kt"
                        }

                        window.location.href =
                            "https://github.com/dioxic/Muon/tree/kotlin-mui/src/jsMain/kotlin/uk/dioxic/muon/component/page/$name"
                    }

                    GitHub()
                }
            }
            Box {
                sx {
                    position = Position.relative
                }
                Tooltip {
                    title = ReactNode("Reindex Rekordbox Database")

                    IconButton {
                        ariaLabel = "reindex"
                        ariaHasPopup = `false`
                        size = Size.large
                        color = IconButtonColor.inherit
                        disabled = indexing == true
                        onClick = {
                            setIndexing(true)
                            MainScope().promise {
                                Api.get<String>("${Routes.index}/refresh")
                            }.then {
                                addAlert(uk.dioxic.muon.context.Alert.AlertSuccess(it))
                                setIndexing(false)
                            }
                        }

                        LibraryMusic()
                    }
                }
                if (indexing) {
                    CircularProgress {
                        size = 38.px
                        color = CircularProgressColor.success

                        sx {
                            position = Position.absolute
                            top = 5.px
                            left = 5.px
                            zIndex = integer(1)
                        }

                        variant = CircularProgressVariant.indeterminate
                    }
                }
            }
        }
    }
}
