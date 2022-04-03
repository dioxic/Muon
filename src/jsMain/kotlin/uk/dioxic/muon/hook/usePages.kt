package uk.dioxic.muon.hook

import react.useMemo
import uk.dioxic.muon.component.page.ImportPage
import uk.dioxic.muon.component.page.LibraryPage
import uk.dioxic.muon.component.page.SettingsPage
import uk.dioxic.muon.entity.Page
import uk.dioxic.muon.entity.Pages

fun usePages(): Pages = useMemo {
    setOf(
        Page("settings", "Settings", SettingsPage),
        Page("library", "Library", LibraryPage),
        Page("import", "Import", ImportPage)
    )
}
