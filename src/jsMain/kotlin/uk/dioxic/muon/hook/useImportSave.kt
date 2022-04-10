package uk.dioxic.muon.hook

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import kotlinx.js.jso
import react.query.useMutation
import uk.dioxic.muon.Routes
import uk.dioxic.muon.api.Api
import uk.dioxic.muon.common.QueryKey
import uk.dioxic.muon.model.Track
import uk.dioxic.muon.utils.listModifyMutationOptions
import kotlin.js.Promise

typealias SaveTrack = (Track) -> Unit

fun useImportSave(): SaveTrack {
    val mutation = useMutation(
        mutationFn = ::saveTrack,
        options = listModifyMutationOptions(QueryKey.IMPORT)
    )
    return { track ->
        mutation.mutate(track, jso())
    }
}

private fun saveTrack(track: Track): Promise<Track> =
    MainScope().promise {
        Api.patch(Routes.import, track)
    }