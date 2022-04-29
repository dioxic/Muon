package uk.dioxic.muon.hook

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import react.query.useQuery
import uk.dioxic.muon.Routes
import uk.dioxic.muon.api.Api
import uk.dioxic.muon.common.QueryKey
import uk.dioxic.muon.model.Tracks
import uk.dioxic.muon.utils.defaultQueryOptions

fun useTrackSearch(text: String) = useQuery(
    queryKey = QueryKey.trackSearch(text),
    queryFn = { searchLibrary(text) },
    options = defaultQueryOptions(QueryKey.trackSearch(text))
)

private fun searchLibrary(text: String) =
    MainScope().promise {
        Api.get<Tracks>(Routes.track, "q" to text)
    }