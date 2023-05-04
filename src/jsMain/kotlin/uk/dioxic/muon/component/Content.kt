package uk.dioxic.muon.component

import mui.system.Box
import mui.system.sx
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.router.Outlet
import uk.dioxic.muon.common.Area
import web.cssom.px

val Content = FC<Props> {
    Box {
        component = ReactHTML.main
        sx {
            gridArea = Area.Content
            padding = 30.px
        }
        Outlet()
    }
}