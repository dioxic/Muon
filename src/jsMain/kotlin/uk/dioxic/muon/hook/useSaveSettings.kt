package uk.dioxic.muon.hook

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import kotlinx.js.jso
import react.query.useMutation
import uk.dioxic.muon.api.Api
import uk.dioxic.muon.common.QueryKey
import uk.dioxic.muon.config.Settings
import uk.dioxic.muon.route.Routes
import uk.dioxic.muon.utils.defaultMutationOptions
import kotlin.js.Promise

typealias SaveSettings = (Settings) -> Unit

fun useSaveSettings(): SaveSettings {
    val mutation = useMutation(
        mutationFn = ::saveSettings,
        options = defaultMutationOptions(QueryKey.SETTINGS)
    )
    return { settings ->
        mutation.mutate(settings, jso())
    }
}

private fun saveSettings(settings: Settings): Promise<Settings> =
    MainScope().promise {
        Api.put(Routes.settings, settings)
    }
