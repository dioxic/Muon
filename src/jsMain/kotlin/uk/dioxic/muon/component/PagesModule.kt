package uk.dioxic.muon.component

import react.FC
import react.PropsWithChildren
import react.createContext
import uk.dioxic.muon.hook.usePages
import uk.dioxic.muon.entity.Pages

val PagesContext = createContext<Pages>()

val PagesModule = FC<PropsWithChildren> { props ->
    val users = usePages()

    PagesContext.Provider(users) {
        +props.children
    }
}
