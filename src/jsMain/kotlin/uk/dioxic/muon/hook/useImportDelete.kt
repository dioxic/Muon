package uk.dioxic.muon.hook

import js.core.jso
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import tanstack.react.query.useMutation
import uk.dioxic.muon.Routes
import uk.dioxic.muon.api.Api
import uk.dioxic.muon.common.QueryKeys
import uk.dioxic.muon.model.Track
import uk.dioxic.muon.utils.listDeleteMutationOptions
import kotlin.js.Promise

typealias DeleteTrack = (Track) -> Unit

fun useImportDelete(): DeleteTrack {
    val mutation = useMutation(
        mutationFn = ::deleteTrack,
        options = listDeleteMutationOptions(QueryKeys.IMPORT)
    )
    return { track ->
        mutation.mutate(track, jso())
    }
}

private fun deleteTrack(track: Track): Promise<Unit> =
    MainScope().promise {
        Api.delete(Routes.trackApi(track))
    }