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

fun useTrackSearch(text: String) = useQuery(
    queryKey = QueryKeys.trackSearch(text),
    queryFn = { searchLibrary(text) },
    options = defaultQueryOptions(QueryKeys.trackSearch(text))
)

private fun searchLibrary(text: String) =
    MainScope().promise {
        Api.get<ReadonlyArray<Track>>(Routes.track, "q" to text)
    }
