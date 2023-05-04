package uk.dioxic.muon.component.page

import js.core.jso
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mui.material.*
import mui.system.responsive
import mui.system.sx
import org.w3c.dom.HTMLInputElement
import react.*
import react.dom.events.FormEvent
import react.dom.html.ReactHTML
import react.dom.onChange
import uk.dioxic.muon.common.percent
import uk.dioxic.muon.context.SettingsContext
import uk.dioxic.muon.hook.useSettingsSave
import web.cssom.Display
import web.cssom.FontFamily
import web.cssom.FontSize
import web.cssom.px
import web.html.HTMLDivElement
import web.html.InputType

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

fun FormEvent<HTMLDivElement>.htmlInputValue() =
    (target.asDynamic() as HTMLInputElement).value

val SettingsPage = VFC {
    val settings = useContext(SettingsContext)!!
    val saveSettings = useSettingsSave()
    val (form, setForm) = useState(settings)

    useEffect(settings) {
        if (settings != form) {
            setForm(settings)
        }
    }

    fun handleSave() {
        if (settings != form) {
            saveSettings(form)
        }
    }

    fun setLocalFolderMapping(rbFolder: String, localFolder: String) {
        setForm(
            form.copy(
                folderMappings = if (rbFolder.isEmpty() && localFolder.isEmpty())
                    emptyList()
                else
                    listOf(rbFolder to localFolder)
            )
        )
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
                    saveSettings(form.copy(theme = event.target.value))
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
            value = form.importDir
            onChange = { event ->
                setForm(form.copy(importDir = event.htmlInputValue()))
            }
            onBlur = { _ -> handleSave() }

            +defaultTextProps
        }
        TextField {
            id = "downloadDir"
            label = ReactNode("Download Folder")
            value = form.downloadDirs.firstOrNull().orEmpty()
            onChange = { event ->
                setForm(form.copy(downloadDirs = listOf(event.htmlInputValue())))
            }
            onBlur = { _ -> handleSave() }

            +defaultTextProps
        }
        TextField {
            id = "rbDatabase"
            label = ReactNode("Rekordbox Database")
            value = form.rekordboxDatabase
            onChange = { event ->
                setForm(form.copy(rekordboxDatabase = event.htmlInputValue()))
            }
            onBlur = { _ -> handleSave() }

            +defaultTextProps
        }
        TextField {
            id = "deleteDir"
            label = ReactNode("Recycle Bin Folder")
            value = form.deleteDir
            onChange = { event ->
                setForm(form.copy(deleteDir = event.htmlInputValue()))
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
                value = form.folderMappings.firstOrNull()?.first.orEmpty()
                onChange = { event ->
                    setLocalFolderMapping(
                        rbFolder = event.htmlInputValue(),
                        localFolder = form.folderMappings.firstOrNull()?.second ?: ""
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
                value = form.folderMappings.firstOrNull()?.second.orEmpty()
                onChange = { event ->
                    setLocalFolderMapping(
                        rbFolder = form.folderMappings.firstOrNull()?.first ?: "",
                        localFolder = event.htmlInputValue()
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