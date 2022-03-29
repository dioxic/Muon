package uk.dioxic.muon.component

import csstype.Color
import csstype.None.none
import emotion.react.css
import mui.material.*
import mui.system.Box
import mui.system.sx
import react.FC
import react.Props
import react.ReactNode
import react.dom.html.ReactHTML
import react.router.dom.NavLink
import react.router.useLocation
import react.useContext
import uk.dioxic.muon.common.Area
import uk.dioxic.muon.common.Sizes.Sidebar
import uk.dioxic.muon.context.PagesContext

val Sidebar = FC<Props> {
    val pages = useContext(PagesContext)
    val lastPathname = useLocation().pathname.substringAfterLast("/")

    Box {
        component = ReactHTML.nav
        sx {
            gridArea = Area.Sidebar
        }

        Drawer {
            variant = DrawerVariant.permanent
            anchor = DrawerAnchor.left

            Toolbar()

            List {
                sx { width = Sidebar.Width }

                for ((key, name) in pages) {
                    NavLink {
                        to = key

                        css {
                            textDecoration = none
                            color = Color.currentcolor
                        }

                        ListItemButton {
                            selected = lastPathname == key

                            ListItemText {
                                primary = ReactNode(name)
                            }
                        }
                    }
                }
            }
        }
    }
}
