package uk.dioxic.muon.hook

import js.core.ReadonlyArray
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import tanstack.query.core.QueryKey
import tanstack.react.query.useQuery
import uk.dioxic.muon.Routes
import uk.dioxic.muon.api.Api
import uk.dioxic.muon.common.QueryKeys
import uk.dioxic.muon.model.Track
import kotlin.js.Promise

fun useImportFetchOld() = useQuery<ReadonlyArray<Track>, Error, ReadonlyArray<Track>, QueryKey>(
    queryKey = QueryKeys.IMPORT,
    queryFn = { readImports() },
//    options = defaultQueryOptions(QueryKeys.IMPORT)
)

fun useImportFetch(): ReadonlyArray<Track> {
    val result = useQuery<ReadonlyArray<Track>, Error, ReadonlyArray<Track>, QueryKey>(
        queryKey = QueryKeys.IMPORT,
        queryFn = { readImports() },
    )
    return result.data ?: emptyArray()
}



private fun readImports(): Promise<ReadonlyArray<Track>> =
    MainScope().promise {
        Api.get(Routes.import)
    }
