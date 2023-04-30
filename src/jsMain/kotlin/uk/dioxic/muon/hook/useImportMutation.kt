package uk.dioxic.muon.hook

import io.ktor.client.plugins.*
import js.core.jso
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import tanstack.react.query.UseMutationOptions
import tanstack.react.query.UseMutationResult
import tanstack.react.query.useMutation
import tanstack.react.query.useQueryClient
import react.useContext
import uk.dioxic.muon.Routes
import uk.dioxic.muon.api.Api
import uk.dioxic.muon.common.QueryKeys
import uk.dioxic.muon.context.Alert
import uk.dioxic.muon.context.AlertContext
import uk.dioxic.muon.model.ImportResponse
import uk.dioxic.muon.model.Track
import uk.dioxic.muon.model.Tracks
import uk.dioxic.muon.utils.errMsg
import kotlin.js.Promise

fun useImportMutation(): UseMutationResult<ImportResponse, ResponseException, Tracks, Unit> {
    val queryClient = useQueryClient()
    val (_, addAlert) = useContext(AlertContext)!!

    fun importTrack(tracks: Tracks): Promise<ImportResponse> =
        MainScope().promise {
            Api.post(
                path = Routes.import,
                data = tracks
            )
        }

    val mutation = useMutation(
        mutationFn = ::importTrack,
        options = jso<UseMutationOptions<ImportResponse, ResponseException, Tracks, Unit>> {
            onError = { error, _, _ ->
                addAlert(Alert.AlertError(errMsg(error)))
                null
            }
            onSuccess = { response, _, _ ->
                queryClient.setQueryData<List<Track>>(QueryKeys.IMPORT,
                    { tracks -> tracks?.filterNot { response.successes.contains(it.id) } ?: emptyList() },
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