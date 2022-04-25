package uk.dioxic.muon.hook

import io.ktor.client.plugins.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import kotlinx.js.jso
import react.query.UseMutationOptions
import react.query.useMutation
import react.query.useQueryClient
import react.useContext
import uk.dioxic.muon.Routes
import uk.dioxic.muon.api.Api
import uk.dioxic.muon.common.QueryKey
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
    val (_, addAlert) = useContext(AlertContext)

    return jso {
        onMutate = { newValue ->
            // Cancel any outgoing refetches (so they don't overwrite our optimistic update)
            queryClient.cancelQueries(QueryKey.IMPORT)

            // Snapshot the previous value
            val previousValue = queryClient.getQueryData<Tracks>(QueryKey.IMPORT)

            // Optimistically remove the duplicate tracks
            queryClient.setQueryData<Tracks>(
                queryKey = QueryKey.IMPORT,
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
            queryClient.setQueryData<Tracks>(QueryKey.IMPORT, { previousValue!! }, jso())
            null
        }
    }
}