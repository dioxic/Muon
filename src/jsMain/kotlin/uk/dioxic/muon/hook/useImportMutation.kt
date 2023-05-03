package uk.dioxic.muon.hook

import io.ktor.client.plugins.*
import js.core.jso
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import react.useContext
import tanstack.react.query.UseMutationOptions
import tanstack.react.query.UseMutationResult
import tanstack.react.query.useMutation
import tanstack.react.query.useQueryClient
import uk.dioxic.muon.Routes
import uk.dioxic.muon.api.Api
import uk.dioxic.muon.common.QueryKeys
import uk.dioxic.muon.context.Alert
import uk.dioxic.muon.context.AlertContext
import uk.dioxic.muon.model.ImportResponse
import uk.dioxic.muon.model.Track
import uk.dioxic.muon.utils.errMsg
import kotlin.js.Promise

fun useImportMutation(): UseMutationResult<ImportResponse, ResponseException, Array<out Track>, Unit> {
    val queryClient = useQueryClient()
    val (_, addAlert) = useContext(AlertContext)!!

    fun importTrack(tracks: Array<out Track>): Promise<ImportResponse> =
        MainScope().promise {
            Api.post(
                path = Routes.import,
                data = tracks.map { track -> Track.EMPTY.copy(id = track.id) }
            )
        }

    val mutation = useMutation(
        mutationFn = ::importTrack,
        options = jso<UseMutationOptions<ImportResponse, ResponseException, Array<out Track>, Unit>> {
            onError = { error, _, _ ->
                addAlert(Alert.AlertError(errMsg(error)))
                null
            }
            onSuccess = { response, _, _ ->
                queryClient.setQueryData<Array<out Track>>(QueryKeys.IMPORT,
                    { tracks -> tracks?.filterNot { response.successes.contains(it.id) }?.toTypedArray() ?: emptyArray() },
                    jso())

                val errorCount = response.errors.size
                when {
                    errorCount == 1 -> addAlert(Alert.AlertError(response.errors.values.first()))
                    errorCount > 1 -> addAlert(Alert.AlertError("Failed to import $errorCount tracks"))
                }
                null
            }
        }
    )
    return mutation
}