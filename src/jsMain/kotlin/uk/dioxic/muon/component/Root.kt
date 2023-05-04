package uk.dioxic.muon.component

import mui.system.Box
import mui.system.sx
import react.VFC
import remix.run.router.LoaderFunction
import uk.dioxic.muon.common.Area
import uk.dioxic.muon.common.Sizes
import uk.dioxic.muon.entity.PAGES
import web.cssom.Auto
import web.cssom.Display
import web.cssom.GridTemplateAreas
import web.cssom.array
import kotlin.js.Promise.Companion.resolve

val Root = VFC {
    Box {
        sx {
            display = Display.grid
            gridTemplateRows = array(
                Sizes.Header.Height,
                Auto.auto,
            )
            gridTemplateColumns = array(
                Sizes.Sidebar.Width, Auto.auto,
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

val pagesLoader: LoaderFunction = {
    resolve(PAGES)
}