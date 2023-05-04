package uk.dioxic.muon.entity

import react.VFC
import uk.dioxic.muon.component.page.ImportPage
import uk.dioxic.muon.component.page.LibraryPage
import uk.dioxic.muon.component.page.SettingsPage

val PAGES = setOf(
    Page("settings", "Settings", SettingsPage),
    Page("library", "Library", LibraryPage),
    Page("import", "Import", ImportPage)
)

data class Page(
    val key: String,
    val name: String,
    val Component: VFC,
)

typealias Pages = Iterable<Page>