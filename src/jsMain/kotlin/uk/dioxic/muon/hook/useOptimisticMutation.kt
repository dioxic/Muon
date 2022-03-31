package uk.dioxic.muon.hook

import io.ktor.client.plugins.*
import kotlinx.js.jso
import react.query.MutationFunction
import react.query.UseMutationResult
import react.query.useMutation
import react.query.useQueryClient
import react.useContext
import uk.dioxic.muon.QueryKey
import uk.dioxic.muon.context.Alert
import uk.dioxic.muon.context.AlertContext
import kotlin.js.Promise

fun <TData> useOptimisticMutation(
    queryKey: QueryKey,
    mutationFn: MutationFunction<Unit, TData>
): UseMutationResult<Unit, ResponseException, TData, TData> {
    val queryClient = useQueryClient()
    val (_, addAlert) = useContext(AlertContext)

    return useMutation(
        mutationFn = mutationFn,
        options = jso {
            onMutate = { newValue ->
                // Cancel any outgoing refetches (so they don't overwrite our optimistic update)
                queryClient.cancelQueries(queryKey.name)

                // Snapshot the previous value
                val previousSettings = queryClient.getQueryData<TData>(queryKey.name)

                // Optimistically update to the new value
                queryClient.setQueryData<TData>(queryKey.name, { newValue }, jso())

                // Set the previous settings value to the context
                Promise.resolve(previousSettings)
            }
            onError = { error, _, previousSettings ->
                addAlert(Alert.AlertError("Error saving settings - ${error.response.status.description}"))
                queryClient.setQueryData<TData>(queryKey.name, { previousSettings!! }, jso())
                null
            }
        }
    )
}