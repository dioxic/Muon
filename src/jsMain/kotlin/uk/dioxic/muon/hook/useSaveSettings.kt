package uk.dioxic.muon.hook

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import kotlinx.js.jso
import react.query.useMutation
import react.query.useQueryClient
import uk.dioxic.muon.QueryKey
import uk.dioxic.muon.api.Api
import uk.dioxic.muon.config.Settings
import uk.dioxic.muon.route.Routes
import kotlin.js.Promise

typealias SaveSettings = (Settings) -> Unit

fun useSaveSettings(): SaveSettings {
    val queryClient = useQueryClient()
    val mutation = useMutation<Settings, Error, Settings, Settings>(
        mutationFn = { settings -> saveSettings(settings) },
        options = jso {
//            onSuccess = { _, _, _ -> queryClient.invalidateQueries<Nothing>(QueryKey.SETTINGS.name) }
            onMutate = { newSettings ->
                // Cancel any outgoing refetches (so they don't overwrite our optimistic update)
                queryClient.cancelQueries(QueryKey.SETTINGS.name)

                // Snapshot the previous value
                val previousSettings = queryClient.getQueryData<Settings>(QueryKey.SETTINGS.name)

                // Optimistically update to the new value
                queryClient.setQueryData<Settings>(QueryKey.SETTINGS.name, { newSettings }, jso())

                Promise.resolve(previousSettings)
            }
            onError = { err, newSettings, previousSettings ->
                println("error here")
                println("previous state: $previousSettings")
                queryClient.setQueryData<Settings>(QueryKey.SETTINGS.name, { previousSettings!! }, jso())
                null
            }
        }
    )
    return { settings ->
        mutation.mutate(settings, jso())
    }
}

fun saveSettings(settings: Settings): Promise<Settings> =
    MainScope().promise {
        Api.post(Routes.settings, settings)
    }.then { settings }
