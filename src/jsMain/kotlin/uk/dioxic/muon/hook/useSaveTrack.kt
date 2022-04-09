package uk.dioxic.muon.hook

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import kotlinx.js.jso
import react.query.useMutation
import uk.dioxic.muon.api.Api
import uk.dioxic.muon.common.QueryKey
import uk.dioxic.muon.model.Track
import uk.dioxic.muon.route.Routes
import uk.dioxic.muon.utils.defaultListMutationOptions
import kotlin.js.Promise

typealias SaveTrack = (Track) -> Unit

fun useSaveTrack(): SaveTrack {
    val mutation = useMutation(
        mutationFn = ::saveTrack,
        options = defaultListMutationOptions(QueryKey.IMPORT)
    )
    return { track ->
        mutation.mutate(track, jso())
    }
}

private fun saveTrack(track: Track): Promise<Track> =
    MainScope().promise {
        Api.patch(Routes.import, track)
    }