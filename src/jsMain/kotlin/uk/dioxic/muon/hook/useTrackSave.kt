package uk.dioxic.muon.hook

import js.core.jso
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import tanstack.react.query.useMutation
import uk.dioxic.muon.Routes
import uk.dioxic.muon.api.Api
import uk.dioxic.muon.common.QueryKeys
import uk.dioxic.muon.model.Track
import uk.dioxic.muon.utils.listModifyMutationOptions
import kotlin.js.Promise

typealias SaveTrack = (Track) -> Unit

fun useTrackSave(): SaveTrack {
    val mutation = useMutation(
        mutationFn = ::saveTrack,
        options = listModifyMutationOptions(QueryKeys.IMPORT)
    )
    return { track ->
        mutation.mutate(track, jso())
    }
}

private fun saveTrack(track: Track): Promise<Track> =
    MainScope().promise {
        Api.patch(Routes.track, track)
    }