package uk.dioxic.muon


import js.core.jso
import react.FC
import react.Props
import react.create
import react.dom.client.createRoot
import react.router.RouterProvider
import react.router.dom.createHashRouter
import tanstack.query.core.QueryClient
import tanstack.react.query.QueryClientProvider
import uk.dioxic.muon.component.*
import uk.dioxic.muon.context.AlertModule
import uk.dioxic.muon.context.AudioPlayerModule
import uk.dioxic.muon.context.ThemeModule
import web.dom.document

fun main() {
    createRoot(document.createElement("div")
        .also { document.body.appendChild(it) })
        .render(App.create())
}

private val hashRouter = createHashRouter(
    routes = arrayOf(
        jso {
            path = "/"
            loader = pagesLoader
            Component = Root
            ErrorBoundary = Error
            children = arrayOf(
                jso {
                    path = ":pageId"
                    loader = pageLoader
                    Component = Page
                    ErrorBoundary = Error
                },
                jso {
                    path = "*"
                    Component = Error
                }
            )
        }
    )
)

private val simpleRoute = createHashRouter(
    routes = arrayOf(
        jso {
            path = "/"
            Component = Placeholder
            ErrorBoundary = Error
        }
    )
)

private val queryClient = QueryClient()

private val App = FC<Props> {
    QueryClientProvider {
        client = queryClient
        AlertModule {
            ThemeModule {
                AudioPlayerModule {
                    RouterProvider {
                        router = hashRouter
                    }
                }
            }
        }
    }
}