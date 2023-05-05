package uk.dioxic.muon.context

import mui.material.Backdrop
import mui.material.CircularProgress
import mui.material.CircularProgressColor
import mui.system.sx
import react.FC
import react.PropsWithChildren
import react.createContext
import uk.dioxic.muon.config.Settings
import uk.dioxic.muon.hook.SaveSettings
import uk.dioxic.muon.hook.useSettingsFetch
import uk.dioxic.muon.hook.useSettingsSave
import uk.dioxic.muon.model.SettingsResponse
import uk.dioxic.muon.model.ValidationErrors
import web.cssom.Color
import web.cssom.integer

val SettingsContext = createContext<SettingsContextDto>()

data class SettingsContextDto(
    val settings: Settings,
    val saveSettings: SaveSettings,
    val validationErrors: ValidationErrors,
)

val SettingsModule = FC<PropsWithChildren> { props ->
    val queryResult = useSettingsFetch()
    val saveSettings = useSettingsSave()

    Backdrop {
        open = queryResult.isLoading
        sx {
            color = Color("#FFFFFF")
            zIndex = integer(2_000)
        }

        CircularProgress {
            color = CircularProgressColor.inherit
        }
    }

    if (queryResult.isSuccess) {
        val (settings, validationErrors) = queryResult.data!!
        SettingsContext.Provider(SettingsContextDto(settings, saveSettings, validationErrors)) {
            +props.children
        }
    }
}