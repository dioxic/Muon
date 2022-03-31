package uk.dioxic.muon.hook

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import react.query.useQuery
import uk.dioxic.muon.QueryKey
import uk.dioxic.muon.api.Api
import uk.dioxic.muon.model.Track
import uk.dioxic.muon.route.Routes
import uk.dioxic.muon.utils.defaultQueryOptions

typealias Tracks = List<Track>

fun useImport() = useQuery(
    queryKey = QueryKey.IMPORT.name,
    queryFn = { readSettings() },
    options = defaultQueryOptions(QueryKey.IMPORT)
)

private fun readSettings() =
    MainScope().promise {
        Api.get<Tracks>(Routes.import)
    }