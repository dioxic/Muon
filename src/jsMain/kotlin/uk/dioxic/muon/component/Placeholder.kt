package uk.dioxic.muon.component

import csstype.*
import emotion.react.css
import mui.material.Box
import mui.material.Typography
import mui.material.TypographyAlign
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.FC
import react.Props
import react.dom.html.ReactHTML.img
import web.cssom.*

val Placeholder = FC<Props> {
    Box {
        sx {
            display = Display.grid
            justifyContent = JustifyContent.center
            gridTemplateRows = array(0.fr, 0.fr)
        }

        Typography {
            variant = TypographyVariant.h6
            align = TypographyAlign.center

            +"Select something to do"
        }

        img {
            css {
                width = 450.px
                transform = scale(1, -1)
            }

            src = "arrow.png"
        }
    }
}
