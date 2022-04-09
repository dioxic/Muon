package uk.dioxic.muon

import csstype.*
import kotlinx.browser.document
import mui.material.Backdrop
import mui.material.CircularProgress
import mui.material.CircularProgressColor
import mui.system.Box
import mui.system.sx
import react.FC
import react.Props
import react.create
import react.dom.client.createRoot
import react.query.QueryClient
import react.query.QueryClientProvider
import react.router.dom.HashRouter
import uk.dioxic.muon.common.Area
import uk.dioxic.muon.common.Sizes.Header
import uk.dioxic.muon.common.Sizes.Sidebar
import uk.dioxic.muon.component.*
import uk.dioxic.muon.context.AlertModule
import uk.dioxic.muon.context.PagesModule
import uk.dioxic.muon.context.ThemeModule
import uk.dioxic.muon.hook.useSettings

fun main() {
    createRoot(document.createElement("div").also { document.body!!.appendChild(it) })
        .render(App.create())
}

private val App = FC<Props> {
    QueryClientProvider {
        client = queryClient
        HashRouter {
            AlertModule {
                ThemeModule {
                    PagesModule {
                        Box {
                            sx {
                                display = Display.grid
                                gridTemplateRows = array(
                                    Header.Height,
                                    Auto.auto,
                                )
                                gridTemplateColumns = array(
                                    Sidebar.Width, Auto.auto,
                                )
                                gridTemplateAreas = GridTemplateAreas(
                                    arrayOf(Area.Header, Area.Header),
                                    arrayOf(Area.Sidebar, Area.Content),
                                )
                            }

                            Header()
                            Sidebar()
                            Content()
                        }
                    }
                }
            }
        }
    }
}

private val queryClient = QueryClient()
