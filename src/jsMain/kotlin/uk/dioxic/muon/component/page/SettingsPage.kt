package uk.dioxic.muon.component.page

import js.core.jso
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mui.icons.material.Error
import mui.material.*
import mui.system.responsive
import mui.system.sx
import org.w3c.dom.HTMLInputElement
import react.*
import react.dom.events.FormEvent
import react.dom.html.ReactHTML
import react.dom.onChange
import uk.dioxic.muon.common.InputProps
import uk.dioxic.muon.common.percent
import uk.dioxic.muon.config.DirMapping
import uk.dioxic.muon.context.SettingsContext
import uk.dioxic.muon.model.ValidationErrors
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

fun validationAdornment(id: String, errors: ValidationErrors) =
    jso<TextFieldProps> {
        errors.find { it.id == id }?.also {
            error = true
            InputProps = jso {
                endAdornment = InputAdornment.create {
                    position = InputAdornmentPosition.end
                    Tooltip {
                        this.title = ReactNode(it.msg)
                        Error {
                            color = SvgIconColor.error
                        }
                    }
                }
            }
        }
    }

val SettingsPage = VFC {
    val (settings, saveSettings, validationErrors) = useContext(SettingsContext)!!
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

    fun setDirMapping(rbDir: String, hostDir: String) {
        setForm(
            form.copy(
                dirMappings = if (rbDir.isEmpty() && hostDir.isEmpty())
                    emptyList()
                else
                    listOf(DirMapping(rbDir, hostDir))
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
                error = validationErrors.any { it.id == "theme" }
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

            +validationAdornment("importDir", validationErrors)
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

            +validationAdornment("downloadDirs", validationErrors)
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

            +validationAdornment("rekordboxDatabase", validationErrors)
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

            +validationAdornment("deleteDir", validationErrors)
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
                id = "rbDir"
                label = ReactNode("Rekordbox Directory")
                value = form.dirMappings.firstOrNull()?.rbDir.orEmpty()
                onChange = { event ->
                    setDirMapping(
                        rbDir = event.htmlInputValue(),
                        hostDir = form.dirMappings.firstOrNull()?.hostDir ?: ""
                    )
                }
                onBlur = { _ -> handleSave() }

                +validationAdornment("rbDir", validationErrors)
                +defaultTextProps
            }
            TextField {
                sx {
                    width = 50.percent
                }
                id = "hostDir"
                label = ReactNode("Host Directory")
                value = form.dirMappings.firstOrNull()?.hostDir.orEmpty()
                onChange = { event ->
                    setDirMapping(
                        rbDir = form.dirMappings.firstOrNull()?.rbDir ?: "",
                        hostDir = event.htmlInputValue()
                    )
                }
                onBlur = { _ -> handleSave() }

                +validationAdornment("hostDir", validationErrors)
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