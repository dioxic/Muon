package uk.dioxic.muon.component

import react.Props
import react.fc
import react.useState
import uk.dioxic.muon.config.LibraryConfig

external interface SettingsViewProps : Props

val SettingsView = fc<SettingsViewProps> {

    val (libraryConfig, setLibraryConfig) = useState(LibraryConfig.Default)

}