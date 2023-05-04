package uk.dioxic.muon.hook

import io.ktor.client.plugins.*
import js.core.jso
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import react.useContext
import tanstack.react.query.UseMutationOptions
import tanstack.react.query.useMutation
import tanstack.react.query.useQueryClient
import uk.dioxic.muon.Routes
import uk.dioxic.muon.api.Api
import uk.dioxic.muon.common.QueryKeys
import uk.dioxic.muon.config.Settings
import uk.dioxic.muon.context.Alert
import uk.dioxic.muon.context.AlertContext
import uk.dioxic.muon.model.SettingsResponse
import uk.dioxic.muon.utils.errMsg
import kotlin.js.Promise

typealias SaveSettings = (Settings) -> Unit

fun useSettingsSave(): SaveSettings {
    val queryClient = useQueryClient()
    val (_, addAlert) = useContext(AlertContext)!!

    val mutation = useMutation(
        mutationFn = ::saveSettings,
        options = jso<UseMutationOptions<SettingsResponse, ResponseException, Settings, Settings>> {
            onMutate = { newValue ->
                // Cancel any outgoing refetches (so they don't overwrite our optimistic update)
                queryClient.cancelQueries(QueryKeys.SETTINGS)

                // Snapshot the previous value
                val previousValue = queryClient.getQueryData<Settings>(QueryKeys.SETTINGS)

                // Optimistically update to the new value
                queryClient.setQueryData<Settings>(QueryKeys.SETTINGS, { newValue }, jso())

                // Set the previous settings value to the context
                Promise.resolve(previousValue)
            }
            onError = { error, _, previousValue ->
                addAlert(Alert.AlertError(errMsg(error)))
                queryClient.setQueryData<Settings>(QueryKeys.IMPORT, { previousValue!! }, jso())
                null
            }
            onSuccess = { responseValue, _, _ ->
                queryClient.setQueryData<Settings>(QueryKeys.IMPORT, { responseValue.settings }, jso())
                null
            }
        }
    )
    return mutation.mutate.unsafeCast<SaveSettings>()
}

private fun saveSettings(settings: Settings): Promise<SettingsResponse> =
    MainScope().promise {
        Api.put(Routes.settings, settings)
    }
