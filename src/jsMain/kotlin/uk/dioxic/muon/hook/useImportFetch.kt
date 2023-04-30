package uk.dioxic.muon.hook

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import tanstack.react.query.useQuery
import uk.dioxic.muon.Routes
import uk.dioxic.muon.api.Api
import uk.dioxic.muon.common.QueryKeys
import uk.dioxic.muon.model.Tracks
import uk.dioxic.muon.utils.defaultQueryOptions
import kotlin.js.Promise

fun useImportFetch() = useQuery(
    queryKey = QueryKeys.IMPORT,
    queryFn = { readImports() },
    options = defaultQueryOptions(QueryKeys.IMPORT)
)


private fun readImports(): Promise<Tracks> =
    MainScope().promise {
        Api.get(Routes.import)
    }
