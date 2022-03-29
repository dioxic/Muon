package uk.dioxic.muon.context

import csstype.Color
import csstype.integer
import io.ktor.client.plugins.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.js.jso
import mui.material.*
import mui.material.styles.Theme
import mui.material.styles.ThemeProvider
import mui.system.sx
import react.*
import uk.dioxic.muon.api.Api
import uk.dioxic.muon.common.Themes
import uk.dioxic.muon.config.Settings
import uk.dioxic.muon.model.SettingsLoadResponse
import uk.dioxic.muon.model.SettingsSaveResponse
import uk.dioxic.muon.route.Routes

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
    val isSnackbarOpen: Boolean = false,
    val settings: Settings = Settings.DEFAULT,
    val error: String? = null,
)

private sealed class AppEvent {
    object Loading : AppEvent()
    object ClearError : AppEvent()
    object CloseSnackbar : AppEvent()
    data class SetSettings(val settings: Settings) : AppEvent()
    data class Error(val error: String) : AppEvent()
}

private fun stateReducer(state: AppState, event: AppEvent): AppState =
    when (event) {
        is AppEvent.Loading -> state.copy(isLoading = true)
        is AppEvent.SetSettings -> state.copy(isLoading = false, settings = event.settings)
        is AppEvent.Error -> state.copy(isLoading = false, error = event.error, isSnackbarOpen = true)
        is AppEvent.ClearError -> state.copy(error = null)
        is AppEvent.CloseSnackbar -> state.copy(isSnackbarOpen = false)
    }

private fun getTheme(theme: String) =
    Themes.asDynamic()[theme].unsafeCast<Theme>()

val StateModule = FC<PropsWithChildren> { props ->

    val (state, dispatch) = useReducer(::stateReducer, AppState())

    fun saveSettings(settings: Settings): Job {
        return MainScope().launch {
            try {
                val response = Api.post<SettingsSaveResponse>(Routes.settings, settings)
                response.error?.let {
                    dispatch(AppEvent.Error(it))
                }
                dispatch(AppEvent.SetSettings(settings))
            } catch (e: ResponseException) {
                dispatch(AppEvent.Error("${e.response.status.value} - ${e.response.status.description}"))
            }
        }
    }

    fun loadSettings(): Job {
        dispatch(AppEvent.Loading)
        return MainScope().launch {
            try {
                val response = Api.get<SettingsLoadResponse>(Routes.settings)
                response.error?.let {
                    dispatch(AppEvent.Error(it))
                }
                dispatch(AppEvent.SetSettings(response.settings))
            } catch (e: ResponseException) {
                console.error(e)
                dispatch(AppEvent.Error("${e.response.status.value} - ${e.response.status.description}"))
            }
        }
    }

    fun clearError() {
        dispatch(AppEvent.ClearError)
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
                zIndex = integer(2_000)
            }

            CircularProgress {
                color = CircularProgressColor.inherit
            }
        }

        Snackbar {
            open = state.isSnackbarOpen
            onClose = { _, _ -> dispatch(AppEvent.CloseSnackbar) }
            autoHideDuration = 6000
            anchorOrigin = jso {
                horizontal = SnackbarOriginHorizontal.center
                vertical = SnackbarOriginVertical.top
            }
            sx {
                zIndex = integer(2_000)
            }

            Alert {
                severity = AlertColor.error
                onClose = { _ -> dispatch(AppEvent.CloseSnackbar) }

                +state.error.orEmpty()
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
