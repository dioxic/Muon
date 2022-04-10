package uk.dioxic.muon.utils

import io.ktor.client.plugins.*
import kotlinx.js.jso
import react.query.*
import react.useContext
import uk.dioxic.muon.context.Alert
import uk.dioxic.muon.context.AlertContext
import uk.dioxic.muon.model.IdType
import kotlin.js.Promise

fun <TData> defaultQueryOptions(queryKey: QueryKey): UseQueryOptions<TData, ResponseException, TData, QueryKey> {
    val (_, addAlert) = useContext(AlertContext)

    return jso {
        refetchOnWindowFocus = false
        retry = { failureCount, _ -> (failureCount < 1) }
        staleTime = JsDuration.MAX_VALUE
        onError = { error ->
            addAlert(Alert.AlertError("Error fetching $queryKey - ${error.response.status.description}"))
        }
    }
}

fun <TData> defaultMutationOptions(queryKey: QueryKey): UseMutationOptions<TData, ResponseException, TData, TData> {
    val queryClient = useQueryClient()
    val (_, addAlert) = useContext(AlertContext)

    return jso {
        onMutate = { newValue ->
            // Cancel any outgoing refetches (so they don't overwrite our optimistic update)
            queryClient.cancelQueries(queryKey)

            // Snapshot the previous value
            val previousValue = queryClient.getQueryData<TData>(queryKey)

            // Optimistically update to the new value
            queryClient.setQueryData<TData>(queryKey, { newValue }, jso())

            // Set the previous settings value to the context
            Promise.resolve(previousValue)
        }
        onError = { error, _, previousValue ->
            addAlert(Alert.AlertError("Error saving $queryKey - ${error.response.status.description}"))
            queryClient.setQueryData<TData>(queryKey, { previousValue!! }, jso())
            null
        }
        onSuccess = { newValue, _, _ ->
            queryClient.setQueryData<TData>(queryKey, { newValue }, jso())
            null
        }
    }
}

fun <TData : IdType> listModifyMutationOptions(queryKey: QueryKey): UseMutationOptions<TData, ResponseException, TData, TData> {
    val queryClient = useQueryClient()
    val (_, addAlert) = useContext(AlertContext)

    return jso {
        onMutate = { newValue ->
            // Cancel any outgoing refetches (so they don't overwrite our optimistic update)
            queryClient.cancelQueries(queryKey)

            // Snapshot the previous value
            val previousValue = queryClient.getQueryData<List<TData>>(queryKey)
                ?.first { it.id == newValue.id }

            // Optimistically update to the new value
            queryClient.setQueryData<List<TData>>(queryKey, { it.replace(newValue) }, jso())

            // Set the previous settings value to the context
            Promise.resolve(previousValue)
        }
        onError = { error, _, previousValue ->
            addAlert(Alert.AlertError("Error saving $queryKey - ${error.response.status.description}"))
            queryClient.setQueryData<List<TData>>(queryKey, { it.replace(previousValue) }, jso())
            null
        }
        onSuccess = { newValue, _, _ ->
            queryClient.setQueryData<List<TData>>(queryKey, { it.replace(newValue) }, jso())
            null
        }
    }
}

fun <TData : IdType> listDeleteMutationOptions(queryKey: QueryKey): UseMutationOptions<TData, ResponseException, TData, TData> {
    val queryClient = useQueryClient()
    val (_, addAlert) = useContext(AlertContext)

    return jso {
        onMutate = { newValue ->
            // Cancel any outgoing refetches (so they don't overwrite our optimistic update)
            queryClient.cancelQueries(queryKey)

            // Snapshot the previous value
            val previousValue = queryClient.getQueryData<List<TData>>(queryKey)
                ?.first { it.id == newValue.id }

            // Optimistically remove the list element
            queryClient.setQueryData<List<TData>>(queryKey, { it - newValue }, jso())

            // Set the previous settings value to the context
            Promise.resolve(previousValue)
        }
        onError = { error, _, previousValue ->
            addAlert(Alert.AlertError("Error deleting $queryKey - ${error.response.status.description}"))
            queryClient.setQueryData<List<TData>>(queryKey, { it + previousValue }, jso())
            null
        }
        onSuccess = { newValue, _, _ ->
            queryClient.setQueryData<List<TData>>(queryKey, { it + newValue }, jso())
            null
        }
    }
}

private operator fun <T : IdType> List<T>?.plus(item: T?) =
    if (item == null || this == null) {
        emptyList()
    } else {
        this.filterNot { it.id == item.id }
    }

private operator fun <T : IdType> List<T>?.minus(item: T?) =
    if (item == null || this == null) {
        emptyList()
    } else {
        this.filterNot { it.id == item.id }
    }

fun <T : IdType> List<T>?.replace(replacement: T?) =
    if (replacement == null || this == null) {
        emptyList()
    } else {
        this.map { if (it.id == replacement.id) replacement else it }
    }
