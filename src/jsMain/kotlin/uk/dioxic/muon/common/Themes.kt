package uk.dioxic.muon.common

import kotlinx.js.jso
import mui.material.styles.createTheme

object Themes {
    @JsName("light")
    val Light = createTheme(
        jso {
            palette = jso { mode = "light" }
        }
    )

    @JsName("dark")
    val Dark = createTheme(
        jso {
            palette = jso { mode = "dark" }
        }
    )
}
