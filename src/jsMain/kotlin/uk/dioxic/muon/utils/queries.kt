package uk.dioxic.muon.utils

import io.ktor.client.plugins.*
import kotlinx.js.jso
import react.query.JsDuration
import react.query.UseMutationOptions
import react.query.UseQueryOptions
import react.query.useQueryClient
import react.useContext
import uk.dioxic.muon.QueryKey
import uk.dioxic.muon.context.Alert
import uk.dioxic.muon.context.AlertContext
import kotlin.js.Promise

fun <TData> defaultQueryOptions(queryKey: QueryKey): UseQueryOptions<TData, ResponseException, TData, String> {
    val (_, addAlert) = useContext(AlertContext)

    return jso {
        refetchOnWindowFocus = false
        staleTime = JsDuration.MAX_VALUE
        onError = { error ->
            addAlert(Alert.AlertError("Error fetching ${queryKey.name.lowercase()} - ${error.response.status.description}"))
        }
    }
}

fun <TData> defaultMutationOptions(queryKey: QueryKey): UseMutationOptions<Unit, ResponseException, TData, TData> {
    val queryClient = useQueryClient()
    val (_, addAlert) = useContext(AlertContext)

    return jso {
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
            addAlert(Alert.AlertError("Error saving ${queryKey.name.lowercase()} - ${error.response.status.description}"))
            queryClient.setQueryData<TData>(queryKey.name, { previousSettings!! }, jso())
            null
        }
    }
}