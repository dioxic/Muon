package uk.dioxic.muon

import csstype.Auto
import csstype.Display
import csstype.GridTemplateAreas
import csstype.array
import kotlinx.browser.document
import mui.system.Box
import mui.system.sx
import react.FC
import react.Props
import react.create
import react.dom.render
import react.query.QueryClient
import react.query.QueryClientProvider
import react.router.dom.HashRouter
import uk.dioxic.muon.common.Area
import uk.dioxic.muon.common.Sizes.Header
import uk.dioxic.muon.common.Sizes.Sidebar
import uk.dioxic.muon.component.*
import uk.dioxic.muon.context.PagesModule
import uk.dioxic.muon.context.ThemeModule

fun main() {
    render(
        element = App.create(),
        container = document.createElement("div").also { document.body!!.appendChild(it) },
    )
}

private val App = FC<Props> {
    QueryClientProvider {
        client = queryClient
        HashRouter {
            ThemeModule {
//                StateModule {
                PagesModule {

//                        val settings = useSettings()
//
//                        Backdrop {
//                            open = settings.isLoading
//                            sx {
//                                color = Color("#FFFFFF")
//                                zIndex = integer(2_000)
//                            }
//
//                            CircularProgress {
//                                color = CircularProgressColor.inherit
//                            }
//                        }

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
//                }
            }
        }
    }
}

//private val defaultQueryOptions: UseQueryOptions<*, Error, *, *> =
//    jso {
//        refetchOnWindowFocus = false
//    }

private val queryClient = QueryClient()

//jso {
//    defaultOptions = jso { queries = null }
//    defaultOptions = jso {
//        set
//        queries = defaultQueryOptions
//    }
//})