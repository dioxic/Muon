package uk.dioxic.muon.context

import csstype.Color
import csstype.integer
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import mui.material.Backdrop
import mui.material.CircularProgress
import mui.material.CircularProgressColor
import mui.material.CssBaseline
import mui.material.styles.Theme
import mui.material.styles.ThemeProvider
import mui.system.sx
import react.*
import uk.dioxic.muon.api.Api
import uk.dioxic.muon.common.Themes
import uk.dioxic.muon.config.Settings

val AppContext = createContext<AppContextDto>()

data class AppContextDto(
    val settings: Settings,
    val error: String? = null,
    val theme: Theme,
    val saveSettings: (Settings) -> Unit,
    val toggleTheme: () -> Unit
)

data class AppState(
    val isLoading: Boolean = true,
    val settings: Settings = Settings.DEFAULT,
    val error: String? = null,
)

private sealed class AppEvent {
    object Loading : AppEvent()
    data class SetSettings(val settings: Settings) : AppEvent()
    data class Error(val error: String) : AppEvent()
}

private fun stateReducer(state: AppState, event: AppEvent): AppState =
    when (event) {
        is AppEvent.Loading -> state.copy(isLoading = true, error = null)
        is AppEvent.SetSettings -> state.copy(isLoading = false, settings = event.settings)
        is AppEvent.Error -> state.copy(isLoading = false, error = event.error)
    }

private fun getTheme(theme: String) =
    Themes.asDynamic()[theme].unsafeCast<Theme>()

val StateModule = FC<PropsWithChildren> { props ->

    val (state, dispatch) = useReducer(::stateReducer, AppState())

    fun saveSettings(settings: Settings): Job {
        return MainScope().launch {
            try {
                Api.saveSettings(settings)
                dispatch(AppEvent.SetSettings(settings))
            } catch (e: Exception) {
                dispatch(AppEvent.Error("Error saving settings"))
            }
        }
    }

    fun loadSettings(): Job {
        dispatch(AppEvent.Loading)
        return MainScope().launch {
            try {
                dispatch(AppEvent.SetSettings(Api.getSettings()))
            } catch (e: Exception) {
                dispatch(AppEvent.Error("Error loading settings"))
            }
        }
    }

    fun toggleTheme() {
        val themeText = if (state.settings.theme == "dark") "light" else "dark"

        saveSettings(state.settings.copy(theme = themeText))
    }

    useEffectOnce {
        val job = loadSettings()
        cleanup {
            job.cancel()
        }
    }

    AppContext.Provider(
        AppContextDto(
            settings = state.settings,
            error = state.error,
            theme = getTheme(state.settings.theme),
            saveSettings = ::saveSettings,
            toggleTheme = ::toggleTheme,
        )
    ) {
        Backdrop {
            open = state.isLoading
            sx {
                color = Color("#FFFFFF")
                zIndex = integer(1000)
            }

            CircularProgress {
                color = CircularProgressColor.inherit
            }
        }

        if (!state.isLoading) {
            ThemeProvider {
                this.theme = getTheme(state.settings.theme)

                CssBaseline()
                +props.children
            }
        }
    }
}
