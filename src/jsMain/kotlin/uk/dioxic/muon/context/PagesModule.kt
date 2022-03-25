package uk.dioxic.muon.context

import react.FC
import react.PropsWithChildren
import react.createContext
import uk.dioxic.muon.entity.Pages
import uk.dioxic.muon.hook.usePages

val PagesContext = createContext<Pages>()

val PagesModule = FC<PropsWithChildren> { props ->
    val pages = usePages()

    PagesContext.Provider(pages) {
        +props.children
    }
}
