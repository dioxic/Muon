package uk.dioxic.muon.component

import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.mIconButton
import com.ccfraser.muirwik.components.input.mInput
import com.ccfraser.muirwik.components.list.mList
import com.ccfraser.muirwik.components.list.mListItem
import com.ccfraser.muirwik.components.list.mListItemText
import com.ccfraser.muirwik.components.styles.Breakpoint
import com.ccfraser.muirwik.components.styles.down
import com.ccfraser.muirwik.components.styles.fade
import com.ccfraser.muirwik.components.styles.up
import kotlinext.js.js
import kotlinext.js.jsObject
import kotlinx.css.*
import kotlinx.css.properties.Timing
import kotlinx.css.properties.Transition
import kotlinx.css.properties.ms
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import react.*
import styled.StyleSheet
import styled.css
import styled.styledDiv

interface MainFrameProps : RProps {
    var onThemeSwitch: () -> Unit
    var initialView: String
}

private val styles = object : StyleSheet("ComponentStyles", isStatic = true) {
    val searchIcon by css {
        width = 9.spacingUnits
        height = 100.pct
        position = Position.absolute
        pointerEvents = PointerEvents.none
        display = Display.flex
        alignItems = Align.center
        justifyContent = JustifyContent.center
    }
}

val MainFrame = functionalComponent<MainFrameProps> { props ->
    val (view, setView) = useState(props.initialView)
    val (responsiveDrawerOpen, setResponsiveDrawerOpen) = useState(false)
    val (searchText, setSearchText) = useState("")

    val viewList = listOf("Import", "Shopping")

    val drawerWidth = 180.px

    fun handleSearchChange(event: Event) {
        val value = (event.target as HTMLInputElement).value
        if (value.length > 2 || value.isBlank()) {
            setSearchText(value)
        }
    }

    fun handleViewSelect(view: String) {
        setView(view)
    }

    mCssBaseline()

    themeContext.Consumer { theme ->
        val themeStyles = object : StyleSheet("ComponentStyles", isStatic = true) {
            val search by css {
                position = Position.relative
                borderRadius = theme.shape.borderRadius.px
                backgroundColor = Color(fade(theme.palette.common.white, 0.15))
                hover {
                    backgroundColor = Color(fade(theme.palette.common.white, 0.25))
                }
                marginRight = 6.px
                marginLeft = 0.px
                width = 100.pct
                media(theme.breakpoints.up(Breakpoint.sm)) {
                    marginLeft = 1.spacingUnits
                    width = LinearDimension.auto
                }
            }
            val inputInput by css {
                paddingTop = 1.spacingUnits
                paddingRight = 1.spacingUnits
                paddingBottom = 1.spacingUnits
                paddingLeft = 10.spacingUnits
                transition += Transition("width", theme.transitions.duration.standard.ms, Timing.easeInOut, 0.ms)
                width = 100.pct
                media(theme.breakpoints.up(Breakpoint.sm)) {
                    width = 120.px
                    focus {
                        width = 200.px
                    }
                }
            }
        }

        styledDiv {
            css {
                flexGrow = 1.0
                width = 100.pct
                zIndex = 1
                overflow = Overflow.hidden
                position = Position.relative
                display = Display.flex
            }

            styledDiv {
                // App Frame
                css {
                    overflow = Overflow.hidden
                    position = Position.relative
                    display = Display.flex
                    width = 100.pct
                }

                mAppBar(position = MAppBarPosition.absolute) {
                    css {
                        zIndex = theme.zIndex.drawer + 1
                    }
                    mToolbar {
                        mHidden(mdUp = true, implementation = MHiddenImplementation.css) {
                            mIconButton(
                                iconName = "menu",
                                color = MColor.inherit,
                                onClick = { setResponsiveDrawerOpen(true) }
                            )
                        }
                        mToolbarTitle("Music Organisation")
                        styledDiv {
                            css(themeStyles.search)
                            styledDiv {
                                css(styles.searchIcon)
                                mIcon("search")
                            }
                            val inputProps = object : RProps {
                                val className = "${styles.name}-inputInput"
                            }
                            mInput(
                                placeholder = "Search...",
                                disableUnderline = true,
                                onChange = ::handleSearchChange
                            ) {
                                attrs.inputProps = inputProps
                                css {
                                    color = Color.inherit
                                }
                            }
                        }
                        mIconButton("lightbulb_outline", onClick = {
                            props.onThemeSwitch()
                        })
                    }
                }

                val p: MPaperProps = jsObject { }
                p.asDynamic().style = js {
                    position = "relative"
                    width = drawerWidth.value
                    display = "block"
                    height = "100%"
                    minHeight = "100vh"
                }
                mHidden(mdUp = true) {
                    mDrawer(responsiveDrawerOpen,
                        MDrawerAnchor.left,
                        MDrawerVariant.temporary,
                        paperProps = p,
                        onClose = { setResponsiveDrawerOpen(!responsiveDrawerOpen) }
                    ) {
                        spacer()
                        menuItems(
                            view = view,
                            onViewSelect = ::handleViewSelect,
                            viewList = viewList
                        )
                    }
                }
                mHidden(smDown = true, implementation = MHiddenImplementation.css) {
                    mDrawer(true, MDrawerAnchor.left, MDrawerVariant.permanent, paperProps = p) {
                        spacer()
                        menuItems(
                            view = view,
                            onViewSelect = ::handleViewSelect,
                            viewList = viewList
                        )
                    }
                }

                // Main content area
                styledDiv {
                    css {
                        height = 100.pct
                        flexGrow = 1.0; minWidth = 0.px
                        backgroundColor = Color(theme.palette.background.default)
                    }
                    spacer()
                    styledDiv {
                        css {
                            media(theme.breakpoints.down(Breakpoint.sm)) {
                                height = 100.vh - 57.px
                            }
                            media(theme.breakpoints.up(Breakpoint.sm)) {
                                height = 100.vh - 65.px
                            }

                            overflowY = Overflow.auto
                            padding(2.spacingUnits)
                            backgroundColor = Color(theme.palette.background.default)
                        }
                        when (view) {
                            "Import" -> child(MusicTable, props = jsObject {
                                filter = searchText
                            })
                            "Shopping" -> child(ShoppingList)
                        }
                    }
                }
            }
        }
    }

}

private fun RBuilder.menuItems(
    viewList: List<String>,
    view: String,
    onViewSelect: (String) -> Unit
) {
    themeContext.Consumer { theme ->
        mList {
            css {
                media(theme.breakpoints.down(Breakpoint.sm)) {
                    height = 100.vh - 57.px
                }
                media(theme.breakpoints.up(Breakpoint.sm)) {
                    height = 100.vh - 65.px
                }
                overflowY = Overflow.auto
                overflowX = Overflow.hidden
                wordBreak = WordBreak.keepAll
            }

            viewList.sortedWith { a, b ->
                if (a == "Home") -1
                else if (b == "Home") 1
                else a.compareTo(b)
            }.forEach { caption ->
                mListItem(
                    button = true,
                    onClick = { onViewSelect(caption) },
                    key = caption
                ) {
                    mListItemText(caption) {
                        css {
                            paddingRight = 0.px
                            if (caption == view) {
                                descendants {
                                    color = Colors.Blue.shade500
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun RBuilder.spacer() {
    themeContext.Consumer { theme ->
        val themeStyles = object : StyleSheet("ComponentStyles", isStatic = true) {
            val toolbar by css {
                toolbarJsCssToPartialCss(theme.mixins.toolbar)
            }
        }

        // This puts in a spacer to get below the AppBar.
        styledDiv {
            css(themeStyles.toolbar)
        }
        mDivider { }
    }
}