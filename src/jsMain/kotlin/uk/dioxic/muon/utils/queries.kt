package uk.dioxic.muon.utils

import io.ktor.client.plugins.*
import js.core.ReadonlyArray
import js.core.jso
import react.useContext
import tanstack.query.core.JsDuration
import tanstack.query.core.QueryKey
import tanstack.react.query.*
import uk.dioxic.muon.api.InternalServerException
import uk.dioxic.muon.context.Alert
import uk.dioxic.muon.context.AlertContext
import uk.dioxic.muon.model.IdType
import kotlin.js.Promise

fun <TData> defaultQueryOptions(queryKey: QueryKey): UseQueryOptions<TData, Throwable, TData, QueryKey> {
    val (_, addAlert) = useContext(AlertContext)!!

    return jso {
        refetchOnWindowFocus = { _ -> false }
        retry = { failureCount, _ -> (failureCount < 1) }
        staleTime = JsDuration.MAX_VALUE
        onError = { error ->
            when(error) {
                is ResponseException -> addAlert(Alert.AlertError("Error fetching $queryKey - ${error.response.status.description}"))
                else -> addAlert(Alert.AlertError("Error fetching $queryKey - ${error.message}"))
            }
        }
    }
}

fun <TData> optimisticMutationOptions(queryKey: QueryKey): UseMutationOptions<TData, ResponseException, TData, TData> {
    val queryClient = useQueryClient()
    val (_, addAlert) = useContext(AlertContext)!!

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
            addAlert(Alert.AlertError(errMsg(error)))
            queryClient.setQueryData<TData>(queryKey, { previousValue!! }, jso())
            null
        }
        onSuccess = { responseValue, _, _ ->
            queryClient.setQueryData<TData>(queryKey, { responseValue }, jso())
            null
        }
    }
}

fun <TData : IdType> listModifyMutationOptions(queryKey: QueryKey): UseMutationOptions<TData, ResponseException, TData, TData> {
    val queryClient = useQueryClient()
    val (_, addAlert) = useContext(AlertContext)!!

    return jso {
        onMutate = { newValue ->
            // Cancel any outgoing refetches (so they don't overwrite our optimistic update)
            queryClient.cancelQueries(queryKey)

            // Snapshot the previous value
            val previousValue = queryClient.getQueryData<ReadonlyArray<TData>>(queryKey)
                ?.firstOrNull { it.id == newValue.id }

            // Optimistically update to the new value
            queryClient.setQueryData<ReadonlyArray<TData>>(queryKey, { it.replace(newValue) }, jso())

            // Set the previous settings value to the context
            Promise.resolve(previousValue)
        }
        onError = { error, _, previousValue ->
            addAlert(Alert.AlertError(errMsg(error)))
            queryClient.setQueryData<ReadonlyArray<TData>>(queryKey, { it.replace(previousValue) }, jso())
            null
        }
        onSuccess = { responseValue, _, _ ->
            queryClient.setQueryData<ReadonlyArray<TData>>(queryKey, { it.replace(responseValue) }, jso())
            null
        }
    }
}

fun <TData : IdType> listDeleteMutationOptions(queryKey: QueryKey): UseMutationOptions<Unit, ResponseException, TData, TData> {
    val queryClient = useQueryClient()
    val (_, addAlert) = useContext(AlertContext)!!

    return jso {
        onMutate = { newValue ->
            // Cancel any outgoing refetches (so they don't overwrite our optimistic update)
            queryClient.cancelQueries(queryKey)

            // Snapshot the previous value
            val previousValue = queryClient.getQueryData<ReadonlyArray<TData>>(queryKey)
                ?.first { it.id == newValue.id }

            // Optimistically remove the list element
            queryClient.setQueryData<ReadonlyArray<TData>>(queryKey, { it - newValue }, jso())

            // Set the previous settings value to the context
            Promise.resolve(previousValue)
        }
        onError = { error, _, previousValue ->
            addAlert(Alert.AlertError(errMsg(error)))
            queryClient.setQueryData<ReadonlyArray<TData>>(queryKey, { it + previousValue }, jso())
            null
        }
    }
}

fun errMsg(error: ResponseException) =
    if (error is InternalServerException && error.message.isNotBlank()) {
        error.message
    } else {
        error.response.status.description
    }


operator fun <T : IdType> ReadonlyArray<T>?.plus(item: T?) =
    when {
        item == null && this != null -> this
        item != null && this == null -> arrayOf(item)
        item != null && this != null -> {
            val result = ArrayList<T>(size + 1)
            result.addAll(this)
            result.add(item)
            result.toTypedArray()
        }

        else -> emptyArray()
    }

operator fun <T : IdType> ReadonlyArray<T>?.minus(item: T?) =
    when {
        item == null && this != null -> this
        item != null && this != null -> this.filterNot { it.id == item.id }.toTypedArray()
        else -> emptyArray()
    }

operator fun <T : IdType> ReadonlyArray<T>?.minus(list: List<T>?) =
    when {
        list == null && this != null -> this
        list != null && this != null -> {
            val ids = list.map { it.id }
            this.filterNot { ids.contains(it.id) }
                .toTypedArray()
        }

        else -> emptyArray()
    }

fun <T : IdType> ReadonlyArray<T>?.replace(replacement: T?) =
    when {
        replacement == null && this != null -> this
        replacement != null && this != null -> this
            .map { if (it.id == replacement.id) replacement else it }
            .toTypedArray()

        else -> emptyArray()
    }