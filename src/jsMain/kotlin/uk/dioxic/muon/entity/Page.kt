package uk.dioxic.muon.entity

import react.FC
import react.Props

data class Page(
    val key: String,
    val name: String,
    val Component: FC<Props>,
)

typealias Pages = Iterable<Page>
