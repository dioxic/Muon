package uk.dioxic.muon.hook

import io.ktor.client.plugins.*
import js.core.jso
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import tanstack.react.query.UseMutationOptions
import tanstack.react.query.useMutation
import tanstack.react.query.useQueryClient
import react.useContext
import uk.dioxic.muon.Routes
import uk.dioxic.muon.api.Api
import uk.dioxic.muon.common.QueryKeys
import uk.dioxic.muon.context.Alert
import uk.dioxic.muon.context.AlertContext
import uk.dioxic.muon.model.Track
import uk.dioxic.muon.model.Tracks
import uk.dioxic.muon.utils.errMsg
import kotlin.js.Promise

fun useTrackDelete(): DeleteTrack {
    val mutation = useMutation(
        mutationFn = ::deleteTrack,
        options = mutationOptions()
    )
    return { track ->
        mutation.mutate(track, jso())
    }
}

private fun deleteTrack(track: Track): Promise<Unit> =
    MainScope().promise {
        Api.delete(Routes.track, track)
    }

private fun mutationOptions(): UseMutationOptions<Unit, ResponseException, Track, Tracks> {
    val queryClient = useQueryClient()
    val (_, addAlert) = useContext(AlertContext)!!

    return jso {
        onMutate = { newValue ->
            // Cancel any outgoing refetches (so they don't overwrite our optimistic update)
            queryClient.cancelQueries(QueryKeys.IMPORT)

            // Snapshot the previous value
            val previousValue = queryClient.getQueryData<Tracks>(QueryKeys.IMPORT)

            // Optimistically remove the duplicate tracks
            queryClient.setQueryData<Tracks>(
                queryKey = QueryKeys.IMPORT,
                updater = {
                    it?.map { track ->
                        if (track.duplicates != null ){
                            val newDups = track.duplicates.filterNot { dup -> dup.path == newValue.path }
                            if (track.duplicates.size != newDups.size) {
                                return@map track.copy(duplicates = newDups)
                            }
                        }
                        track
                    } ?: emptyList()
                },
                options = jso()
            )

            // Set the previous settings value to the context
            Promise.resolve(previousValue)
        }
        onError = { error, _, previousValue ->
            addAlert(Alert.AlertError(errMsg(error)))
            queryClient.setQueryData<Tracks>(QueryKeys.IMPORT, { previousValue!! }, jso())
            null
        }
    }
}