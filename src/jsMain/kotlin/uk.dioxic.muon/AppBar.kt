package uk.dioxic.muon

import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.mIconButton
import com.ccfraser.muirwik.components.input.mInput
import com.ccfraser.muirwik.components.styles.Breakpoint
import com.ccfraser.muirwik.components.styles.fade
import com.ccfraser.muirwik.components.styles.up
import kotlinx.css.*
import kotlinx.css.properties.Timing
import kotlinx.css.properties.Transition
import kotlinx.css.properties.ms
import react.RProps
import react.functionalComponent
import styled.StyleSheet
import styled.css
import styled.styledDiv

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

val AppBar = functionalComponent<RProps> {
    themeContext.Consumer { theme ->
        val themeStyles = object : StyleSheet("ComponentStyles", isStatic = true) {
            val search by css {
                position = Position.relative
                borderRadius = theme.shape.borderRadius.px
                backgroundColor = Color(fade(theme.palette.common.white, 0.15))
                hover {
                    backgroundColor = Color(fade(theme.palette.common.white, 0.25))
                }
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
        mAppBar(position = MAppBarPosition.static) {
            mToolbar {
                mIconButton("menu", color = MColor.inherit) { css { marginLeft = (-12).px; marginRight = 20.px } }
                mToolbarTitle("Muon")
                styledDiv {
                    css(themeStyles.search)
                    styledDiv {
                        css(styles.searchIcon)
                        mIcon("search")
                    }
                    val inputProps = object : RProps {
                        val className = "${styles.name}-inputInput"
                    }
                    mInput(placeholder = "Search...", disableUnderline = true) {
                        attrs.inputProps = inputProps
                        css {
                            color = Color.inherit
                        }
                    }
                }
            }
        }
    }
}