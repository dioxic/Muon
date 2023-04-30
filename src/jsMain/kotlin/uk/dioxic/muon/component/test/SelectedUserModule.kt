package uk.dioxic.muon.component.test

import react.*
import uk.dioxic.muon.entity.User

val SelectedUserContext = createContext<User>()
val SetSelectedUserContext = createContext<StateSetter<User?>>()

val SelectedUserModule = FC<PropsWithChildren> { props ->
    val (user, setUser) = useState<User>()

    SelectedUserContext(user) {
        SetSelectedUserContext(setUser) {
            +props.children
        }
    }
}
