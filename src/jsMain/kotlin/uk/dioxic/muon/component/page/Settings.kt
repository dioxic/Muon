package uk.dioxic.muon.component.page

import csstype.FontFamily
import csstype.FontSize
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mui.material.Box
import mui.material.Paper
import mui.system.sx
import react.FC
import react.Props
import react.VFC
import react.dom.html.ReactHTML
import uk.dioxic.muon.hook.useSettingsFetch

private val json = Json {
    prettyPrint = true
    isLenient = true
}

val SettingsPage = VFC {
    val settings = useSettingsFetch()

    Box {
        Paper {
            component = ReactHTML.pre
            sx {
                fontSize = FontSize.small
                fontFamily= FontFamily.monospace
            }

            +json.encodeToString(settings.data)
        }
    }
}