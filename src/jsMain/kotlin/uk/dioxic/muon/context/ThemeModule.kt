package uk.dioxic.muon.context

import mui.material.CssBaseline
import mui.material.styles.Theme
import mui.material.styles.ThemeProvider
import react.FC
import react.PropsWithChildren
import react.createContext
import react.useContext
import uk.dioxic.muon.common.Themes

val ThemeContext = createContext<() -> Unit>()

val ThemeModule = FC<PropsWithChildren> { props ->
    val (settings, saveSettings) = useContext(SettingsContext)!!

    fun toggleColorMode() {
        saveSettings(
            settings.copy(
                theme = if (settings.theme == "dark") "light" else "dark"
            )
        )
    }

    ThemeContext.Provider(::toggleColorMode) {
        ThemeProvider {
            this.theme = getTheme(settings.theme)

            CssBaseline()
            +props.children
        }
    }
}

fun getTheme(theme: String) =
    Themes.asDynamic()[theme].unsafeCast<Theme>()