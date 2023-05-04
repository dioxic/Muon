package uk.dioxic.muon.hook

import js.core.jso
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import tanstack.react.query.useMutation
import uk.dioxic.muon.Routes
import uk.dioxic.muon.api.Api
import uk.dioxic.muon.common.QueryKeys
import uk.dioxic.muon.config.Settings
import uk.dioxic.muon.utils.optimisticMutationOptions
import kotlin.js.Promise

typealias SaveSettings = (Settings) -> Unit

fun useSettingsSave(): SaveSettings {
    val mutation = useMutation(
        mutationFn = ::saveSettings,
        options = optimisticMutationOptions(QueryKeys.SETTINGS)
    )
    return { settings ->
        mutation.mutate(settings, jso())
    }
}

private fun saveSettings(settings: Settings): Promise<Settings> =
    MainScope().promise {
        Api.put(Routes.settings, settings)
    }
