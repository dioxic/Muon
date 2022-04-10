package uk.dioxic.muon.hook

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import react.query.useQuery
import uk.dioxic.muon.Routes
import uk.dioxic.muon.api.Api
import uk.dioxic.muon.common.QueryKey
import uk.dioxic.muon.model.Track
import uk.dioxic.muon.utils.defaultQueryOptions

typealias Tracks = List<Track>

fun useImportFetch() = useQuery(
    queryKey = QueryKey.IMPORT,
    queryFn = { readImports() },
    options = defaultQueryOptions(QueryKey.IMPORT)
)

private fun readImports() =
    MainScope().promise {
        Api.get<Tracks>(Routes.import)
    }
