package uk.dioxic.muon.component.page

import csstype.Display
import csstype.FontFamily
import csstype.FontSize
import csstype.px
import kotlinx.js.jso
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mui.material.*
import mui.system.responsive
import mui.system.sx
import org.w3c.dom.HTMLInputElement
import react.ReactNode
import react.VFC
import react.dom.html.InputType
import react.dom.html.ReactHTML
import react.dom.onChange
import react.useEffect
import react.useState
import uk.dioxic.muon.common.percent
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
    val (localSettings, setLocalSettings) = useState(settings)

    useEffect(settings) {
        if (settings != localSettings) {
            setLocalSettings(settings)
        }
    }

    fun handleSave() {
        if (settings != localSettings) {
            saveSettings(localSettings)
        }
    }

    fun setLocalFolderMapping(rbFolder: String, localFolder: String) {
        setLocalSettings(localSettings.copy(
            folderMappings = if (rbFolder.isEmpty() && localFolder.isEmpty())
                emptyList()
            else
                listOf(rbFolder to localFolder)
        ))
    }

    Stack {
        spacing = responsive(18.px)

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
                    saveSettings(localSettings.copy(theme = event.target.value))
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
            value = localSettings.importDir
            onChange = { event ->
                setLocalSettings(localSettings.copy(
                    importDir = (event.target as HTMLInputElement).value
                ))
            }
            onBlur = { _ -> handleSave() }

            +defaultTextProps
        }
        TextField {
            id = "downloadDir"
            label = ReactNode("Download Folder")
            value = localSettings.downloadDirs.firstOrNull()
            onChange = { event ->
                setLocalSettings(localSettings.copy(
                    downloadDirs = listOf((event.target as HTMLInputElement).value)
                ))
            }
            onBlur = { _ -> handleSave() }

            +defaultTextProps
        }
        TextField {
            id = "rbDatabase"
            label = ReactNode("Rekordbox Database")
            value = localSettings.rekordboxDatabase
            onChange = { event ->
                setLocalSettings(localSettings.copy(
                    rekordboxDatabase = (event.target as HTMLInputElement).value
                ))
            }
            onBlur = { _ -> handleSave() }

            +defaultTextProps
        }
        TextField {
            id = "deleteDir"
            label = ReactNode("Recycle Bin Folder")
            value = localSettings.deleteDir
            onChange = { event ->
                setLocalSettings(localSettings.copy(
                    deleteDir = (event.target as HTMLInputElement).value
                ))
            }
            onBlur = { _ -> handleSave() }

            +defaultTextProps
        }
        Divider { +"Folder Mappings" }
        Stack {
            sx {
                display = Display.flex
            }
            direction = responsive(StackDirection.row)
            spacing = responsive(18.px)

            TextField {
                sx {
                    width = 50.percent
                }
                id = "rekordboxFolder"
                label = ReactNode("Rekordbox Folder")
                value = localSettings.folderMappings.firstOrNull()?.first
                onChange = { event ->
                    setLocalFolderMapping(
                        rbFolder = (event.target as HTMLInputElement).value,
                        localFolder = localSettings.folderMappings.firstOrNull()?.second ?: ""
                    )
                }
                onBlur = { _ -> handleSave() }

                +defaultTextProps
            }
            TextField {
                sx {
                    width = 50.percent
                }
                id = "localFolder"
                label = ReactNode("Local Folder")
                value = localSettings.folderMappings.firstOrNull()?.second
                onChange = { event ->
                    setLocalFolderMapping(
                        rbFolder = localSettings.folderMappings.firstOrNull()?.first ?: "",
                        localFolder = (event.target as HTMLInputElement).value
                    )
                }
                onBlur = { _ -> handleSave() }

                +defaultTextProps
            }
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