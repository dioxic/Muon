package uk.dioxic.muon.hook

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import tanstack.react.query.useQuery
import uk.dioxic.muon.Routes
import uk.dioxic.muon.api.Api
import uk.dioxic.muon.common.QueryKeys
import uk.dioxic.muon.model.SettingsResponse
import uk.dioxic.muon.utils.defaultQueryOptions

fun useSettingsFetch() =
    useQuery(
        queryKey = QueryKeys.SETTINGS,
        queryFn = { readSettings() },
        options = defaultQueryOptions(QueryKeys.SETTINGS)
    )

private fun readSettings() =
    MainScope().promise {
        Api.get<SettingsResponse>(Routes.settings)
    }


