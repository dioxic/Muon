package uk.dioxic.muon.hook

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import kotlinx.js.jso
import uk.dioxic.muon.QueryKey
import uk.dioxic.muon.api.Api
import uk.dioxic.muon.config.Settings
import uk.dioxic.muon.route.Routes
import kotlin.js.Promise

typealias SaveSettings = (Settings) -> Unit

fun useSaveSettings(): SaveSettings {
    val mutation = useOptimisticMutation(QueryKey.SETTINGS, ::saveSettings)
    return { settings ->
        mutation.mutate(settings, jso())
    }
}

//fun useSaveSettings(): SaveSettings {
//    val (_,addAlert) = useContext(AlertContext)
//    val queryClient = useQueryClient()
//    val mutation = useMutation<Unit, ResponseException, Settings, Settings>(
//        mutationFn = ::saveSettings,
//        options = jso {
////            onSuccess = { _, _, _ -> queryClient.invalidateQueries<Nothing>(QueryKey.SETTINGS.name) }
//            onMutate = { newSettings ->
//                // Cancel any outgoing refetches (so they don't overwrite our optimistic update)
//                queryClient.cancelQueries(QueryKey.SETTINGS.name)
//
//                // Snapshot the previous value
//                val previousSettings = queryClient.getQueryData<Settings>(QueryKey.SETTINGS.name)
//
//                // Optimistically update to the new value
//                queryClient.setQueryData<Settings>(QueryKey.SETTINGS.name, { newSettings }, jso())
//
//                // Set the previous settings value to the context
//                Promise.resolve(previousSettings)
//            }
//            onError = { error, _, previousSettings ->
//                addAlert(Alert.AlertError("Error saving settings - ${error.response.status.description}"))
//                queryClient.setQueryData<Settings>(QueryKey.SETTINGS.name, { previousSettings!! }, jso())
//                null
//            }
//        }
//    )
//    return { settings ->
//        mutation.mutate(settings, jso())
//    }
//}

private fun saveSettings(settings: Settings): Promise<Unit> =
    MainScope().promise {
        Api.post(Routes.settings, settings)
    }
