package uk.dioxic.muon.component

import js.errors.JsError
import mui.material.Typography
import react.VFC
import react.router.useRouteError

val Error = VFC {
    val error = useRouteError().unsafeCast<JsError>()

    Typography {
        +error.message
    }
}