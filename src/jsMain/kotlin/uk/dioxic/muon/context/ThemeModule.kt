package uk.dioxic.muon.context

import csstype.Color
import csstype.integer
import mui.material.Backdrop
import mui.material.CircularProgress
import mui.material.CircularProgressColor
import mui.material.CssBaseline
import mui.material.styles.Theme
import mui.material.styles.ThemeProvider
import mui.system.sx
import react.FC
import react.PropsWithChildren
import uk.dioxic.muon.common.Themes
import uk.dioxic.muon.hook.useSettings

//typealias ThemeState = StateInstance<Theme>

//val ThemeContext = createContext<ThemeState>()

val ThemeModule = FC<PropsWithChildren> { props ->
    val settings = useSettings()
//    val state = useState(Themes.Light)
//    val (theme) = state

//    println("settings: ${settings.data}")
//    val theme = settings.data?.let { getTheme(it.theme) } ?: Themes.Light
//
//    println("theme: ${JSON.stringify(theme)}")
    println("settings:" +
            "isLoading=${settings.isLoading}," +
            "isFetching=${settings.isFetching}," +
            "isStale=${settings.isStale}," +
            "isSuccess=${settings.isSuccess}," +
            "isPreviousData=${settings.isPreviousData}")

    Backdrop {
        open = settings.isLoading
        sx {
            color = Color("#FFFFFF")
            zIndex = integer(2_000)
        }

        CircularProgress {
            color = CircularProgressColor.inherit
        }
    }

    if (!settings.isLoading) {
        ThemeProvider {
            this.theme = settings.data?.let { getTheme(it.theme) } ?: Themes.Light
//            this.theme = Themes.Light

            CssBaseline()
            +props.children
        }
    }
}

fun getTheme(theme: String) =
    Themes.asDynamic()[theme].unsafeCast<Theme>()