package uk.dioxic.muon.hook

import js.core.Void
import js.core.jso
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import tanstack.query.core.QueryKey
import tanstack.react.query.useMutation
import tanstack.react.query.useQueryClient
import uk.dioxic.muon.Routes
import uk.dioxic.muon.api.Api
import uk.dioxic.muon.common.QueryKeys
import uk.dioxic.muon.common.QueryKeys.IMPORT
import uk.dioxic.muon.common.QueryKeys.USERS_QUERY_KEY
import uk.dioxic.muon.model.Track
import uk.dioxic.muon.utils.listModifyMutationOptions
import kotlin.js.Promise

typealias SaveTrack = (Track) -> Unit

fun useTrackSave(): SaveTrack {
    val client = useQueryClient()
    val mutation = useMutation(
        mutationFn = ::saveTrack,
        options = listModifyMutationOptions(IMPORT)
//        options = jso {
//            onSuccess = { _, _, _ -> client.invalidateQueries<Void>(IMPORT) }
//        }
    )
//    return { track ->
//        mutation.mutate(track, jso())
//    }
    return mutation.mutate.unsafeCast<SaveTrack>()
}

private fun saveTrack(track: Track): Promise<Track> =
    MainScope().promise {
        Api.patch(Routes.track, track)
    }