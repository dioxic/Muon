package uk.dioxic.muon.common

import csstype.Color
import csstype.NamedColor
import kotlinx.js.jso
import mui.material.PaletteMode
import mui.material.styles.createTheme

object Themes {
    @JsName("light")
    val Light = createTheme(
        jso {
            palette = jso {
                mode = PaletteMode.light
                error = jso {
                    main = "#f50057"
                }
                warning = jso {
                    main = "#ff9100"
                }
            }
        }
    )

    @JsName("dark")
    val Dark = createTheme(
        jso {
            palette = jso {
                mode = PaletteMode.dark
                error = jso {
                    main = "#ff4081"
                }
                warning = jso {
                    main = "#ffab40"
                }
            }
        }
    )
}
