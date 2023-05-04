package uk.dioxic.muon.hook

import js.core.ReadonlyArray
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import tanstack.react.query.useQuery
import uk.dioxic.muon.Routes
import uk.dioxic.muon.api.Api
import uk.dioxic.muon.common.QueryKeys
import uk.dioxic.muon.model.Track
import uk.dioxic.muon.utils.defaultQueryOptions
import kotlin.js.Promise

fun useImportFetch() = useQuery(
    queryKey = QueryKeys.IMPORT,
    queryFn = { readImports() },
    options = defaultQueryOptions(QueryKeys.IMPORT)
)

private fun readImports(): Promise<ReadonlyArray<Track>> =
    MainScope().promise {
        Api.get(Routes.import)
    }
