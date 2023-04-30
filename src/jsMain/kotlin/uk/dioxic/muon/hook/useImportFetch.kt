package uk.dioxic.muon.hook

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import tanstack.query.core.QueryKey
import tanstack.react.query.useQuery
import uk.dioxic.muon.Routes
import uk.dioxic.muon.api.Api
import uk.dioxic.muon.common.QueryKeys
import uk.dioxic.muon.model.Track
import kotlin.js.Promise

fun useImportFetchOld() = useQuery<Array<Track>, Error, Array<Track>, QueryKey>(
    queryKey = QueryKeys.IMPORT,
    queryFn = { readImports() },
//    options = defaultQueryOptions(QueryKeys.IMPORT)
)

fun useImportFetch(): Array<Track> {
    val result = useQuery<Array<Track>, Error, Array<Track>, QueryKey>(
        queryKey = QueryKeys.IMPORT,
        queryFn = { readImports() },
    )
    return result.data ?: emptyArray()
}



private fun readImports(): Promise<Array<Track>> =
    MainScope().promise {
        Api.get(Routes.import)
    }
