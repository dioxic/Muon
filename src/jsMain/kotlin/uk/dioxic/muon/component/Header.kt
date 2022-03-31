package uk.dioxic.muon.component

import csstype.integer
import csstype.number
import kotlinx.browser.window
import mui.icons.material.Brightness4
import mui.icons.material.Brightness7
import mui.icons.material.GitHub
import mui.icons.material.MenuBook
import mui.material.*
import mui.system.sx
import react.FC
import react.Props
import react.ReactNode
import react.create
import react.dom.aria.AriaHasPopup.`false`
import react.dom.aria.ariaHasPopup
import react.dom.aria.ariaLabel
import react.dom.html.ReactHTML
import react.router.useLocation
import uk.dioxic.muon.common.Area
import uk.dioxic.muon.hook.useSaveSettings
import uk.dioxic.muon.hook.useSettings

val Header = FC<Props> {
    val settings = useSettings()
    val saveSettings = useSaveSettings()
    val lastPathname = useLocation().pathname.substringAfterLast("/")

    AppBar {
        position = AppBarPosition.fixed
        sx {
            gridArea = Area.Header
            zIndex = integer(1_500)
        }

        Toolbar {
            Typography {
                sx { flexGrow = number(1.0) }
                variant = "h6"
                noWrap = true
                component = ReactHTML.div

                +"Music Organisation"
            }

            Tooltip {
                title = ReactNode("Theme")

                Switch {
                    icon = Brightness7.create()
                    checkedIcon = Brightness4.create()
                    checked = settings.data!!.theme == "dark"
                    ariaLabel = "theme"

                    onChange = { _, _ ->
                        saveSettings(
                            settings.data!!.copy(
                                theme = if (settings.data!!.theme == "dark") "light" else "dark"
                            )
                        )
                    }
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
        }
    }
}
