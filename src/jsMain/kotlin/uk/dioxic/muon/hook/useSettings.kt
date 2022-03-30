package uk.dioxic.muon.hook

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import kotlinx.js.jso
import react.query.JsDuration
import react.query.UseQueryResult
import react.query.useQuery
import uk.dioxic.muon.QueryKey
import uk.dioxic.muon.api.Api
import uk.dioxic.muon.config.Settings
import uk.dioxic.muon.model.SettingsLoadResponse
import uk.dioxic.muon.route.Routes

typealias SettingQueryResult = UseQueryResult<Settings, Error>

fun useSettings(): SettingQueryResult =
    useQuery(
        queryKey = QueryKey.SETTINGS.name,
        queryFn = { readSettings() },
        options = jso {
            refetchOnWindowFocus = false
            staleTime = JsDuration.MAX_VALUE
            optimisticResults
        }
    )

private fun readSettings() =
    MainScope().promise {
        Api.get<SettingsLoadResponse>(Routes.settings).settings
    }


