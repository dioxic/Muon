package uk.dioxic.muon.component

import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.mIconButton
import com.ccfraser.muirwik.components.input.mInput
import com.ccfraser.muirwik.components.styles.Breakpoint
import com.ccfraser.muirwik.components.styles.fade
import com.ccfraser.muirwik.components.styles.up
import kotlinx.css.*
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import react.Props
import react.fc
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

external interface AppBarProps : Props {
    var onSearchSubmit: (String) -> Unit
}

val AppBar = fc<AppBarProps> { props ->

    fun changeHandler(event: Event) {
        val value = (event.target as HTMLInputElement).value
        if  (value.length > 2 || value.isBlank()) {
            props.onSearchSubmit(value)
        }
    }

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

                    val inputProps = object : Props {
                        val className = "${styles.name}-inputInput"
                    }
                    mInput(placeholder = "Search...", disableUnderline = true, onChange = ::changeHandler) {
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