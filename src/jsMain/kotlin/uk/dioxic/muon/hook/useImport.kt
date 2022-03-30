package uk.dioxic.muon.hook

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import kotlinx.js.jso
import react.query.UseQueryResult
import react.query.useQuery
import uk.dioxic.muon.QueryKey
import uk.dioxic.muon.api.Api
import uk.dioxic.muon.model.Track
import uk.dioxic.muon.route.Routes

typealias ImportQueryResult = UseQueryResult<Tracks, Error>

typealias Tracks = List<Track>

fun useImport(): ImportQueryResult =
    useQuery(
        queryKey = QueryKey.IMPORT.name,
        queryFn = { readSettings() },
        options = jso {
            placeholderData = emptyList<Track>()
        }
    )

private fun readSettings() =
    MainScope().promise {
        Api.get<Tracks>(Routes.import)
    }
