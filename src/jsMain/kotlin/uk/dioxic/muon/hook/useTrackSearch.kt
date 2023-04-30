package uk.dioxic.muon.hook

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import tanstack.react.query.useQuery
import uk.dioxic.muon.Routes
import uk.dioxic.muon.api.Api
import uk.dioxic.muon.common.QueryKeys
import uk.dioxic.muon.model.Tracks
import uk.dioxic.muon.utils.defaultQueryOptions

fun useTrackSearch(text: String) = useQuery(
    queryKey = QueryKeys.trackSearch(text),
    queryFn = { searchLibrary(text) },
    options = defaultQueryOptions(QueryKeys.trackSearch(text))
)

private fun searchLibrary(text: String) =
    MainScope().promise {
        Api.get<Tracks>(Routes.track, "q" to text)
    }
