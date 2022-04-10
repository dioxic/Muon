package uk.dioxic.muon.hook

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import kotlinx.js.jso
import react.query.useMutation
import uk.dioxic.muon.Routes
import uk.dioxic.muon.api.Api
import uk.dioxic.muon.common.QueryKey
import uk.dioxic.muon.model.Track
import uk.dioxic.muon.utils.listDeleteMutationOptions
import kotlin.js.Promise

typealias ImportTrack = (Track) -> Unit

fun useImportImport(): ImportTrack {
    val mutation = useMutation(
        mutationFn = ::importTrack,
        options = listDeleteMutationOptions(QueryKey.IMPORT)
    )
    return { track ->
        mutation.mutate(track, jso())
    }
}

private fun importTrack(track: Track): Promise<Track> =
    MainScope().promise {
        Api.post(Routes.import, track)
        track
    }