package uk.dioxic.muon.component

import react.VFC
import react.router.useLoaderData
import remix.run.router.LoaderFunction
import uk.dioxic.muon.entity.PAGES
import uk.dioxic.muon.entity.Page
import kotlin.js.Promise.Companion.resolve

val Page = VFC {
    useLoaderData().unsafeCast<Page>().Component()
}

val pageLoader: LoaderFunction = { args ->
    resolve(PAGES.single { it.key == args.params["pageId"] })
}