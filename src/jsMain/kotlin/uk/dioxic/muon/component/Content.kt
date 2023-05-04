package uk.dioxic.muon.component

import mui.material.Typography
import mui.system.Box
import mui.system.sx
import react.FC
import react.Props
import react.create
import react.dom.html.ReactHTML
import react.router.Outlet
import react.router.Route
import react.router.Routes
import react.useContext
import uk.dioxic.muon.common.Area
import uk.dioxic.muon.context.PagesContext
import web.cssom.px

private val DEFAULT_PADDING = 30.px

// asDynamic everywhere because https://github.com/JetBrains/kotlin-wrappers/issues/1921
val Content = FC<Props> {
    val pages = useContext(PagesContext)

    Routes {
        Route {
            asDynamic().path = "/"
            asDynamic().element = Box.create {
                component = ReactHTML.main
                sx {
                    gridArea = Area.Content
                    padding = DEFAULT_PADDING
                }

                Outlet()
            }

            pages?.forEachIndexed { i, (key, _, Component) ->
                Route {
                    asDynamic().index = i == 0
                    asDynamic().path = key
                    asDynamic().element = Component.create()
                }
            }

            Route {
                asDynamic().path = "*"
                asDynamic().element = Typography.create { +"404 Page Not Found" }
            }
        }
    }
}
