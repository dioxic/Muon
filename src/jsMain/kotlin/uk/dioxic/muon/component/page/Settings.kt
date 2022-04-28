package uk.dioxic.muon.component.page

import csstype.FontFamily
import csstype.FontSize
import csstype.px
import kotlinx.js.jso
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mui.material.*
import mui.system.sx
import org.w3c.dom.HTMLInputElement
import react.ReactNode
import react.VFC
import react.dom.html.InputType
import react.dom.html.ReactHTML
import uk.dioxic.muon.hook.useSettingsFetch
import uk.dioxic.muon.hook.useSettingsSave

private val json = Json {
    prettyPrint = true
    isLenient = true
}

private val defaultTextProps: BaseTextFieldProps = jso {
    type = InputType.text
    spellCheck = false
    variant = FormControlVariant.outlined
    autoComplete = "off"
}

val SettingsPage = VFC {
    val settings = useSettingsFetch().data!!
    val saveSettings = useSettingsSave()

    Stack {

        sx {
            MuiFormControl.root {
                marginTop = 8.px
                marginBottom = 8.px
            }
        }

        FormControl {
            InputLabel {
                id = "theme-select-label"
                +"Theme"
            }
            Select {
                label = ReactNode("Theme")
                labelId = "theme-select-label"
                value = settings.theme.unsafeCast<Nothing?>()
                onChange = { event, _ ->
                    saveSettings(settings.copy(theme = event.target.value))
                }
                MenuItem {
                    value = "dark"
                    +"Dark"
                }
                MenuItem {
                    value = "light"
                    +"Light"
                }
            }
        }
        TextField {
            id = "importDir"
            label = ReactNode("Import Folder")
            defaultValue = settings.importDir
            onBlur = { event ->
                saveSettings(settings.copy(
                    importDir = (event.target as HTMLInputElement).value
                ))
            }

            +defaultTextProps
        }
        TextField {
            id = "downloadDir"
            label = ReactNode("Download Folder")
            defaultValue = settings.downloadDirs.first()
            onBlur = { event ->
                saveSettings(settings.copy(
                    downloadDirs = listOf((event.target as HTMLInputElement).value)
                ))
            }

            +defaultTextProps
        }
        TextField {
            id = "rbDatabase"
            label = ReactNode("Rekordbox Database")
            defaultValue = settings.rekordboxDatabase
            onBlur = { event ->
                saveSettings(settings.copy(
                    rekordboxDatabase = (event.target as HTMLInputElement).value
                ))
            }

            +defaultTextProps
        }
        TextField {
            id = "deleteDir"
            label = ReactNode("Recycle Bin Folder")
            defaultValue = settings.deleteDir
            onBlur = { event ->
                saveSettings(settings.copy(
                    deleteDir = (event.target as HTMLInputElement).value
                ))
            }

            Chip {
                label = ReactNode("Chip")
            }

            +defaultTextProps
        }

        Divider {
            sx {
                paddingTop = 20.px
                paddingBottom = 10.px
            }
            +"Raw Json"
        }

        Paper {
            component = ReactHTML.pre
            sx {
                fontSize = FontSize.small
                fontFamily = FontFamily.monospace
            }

            +json.encodeToString(settings)
        }
    }
}