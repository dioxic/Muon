package uk.dioxic.muon.hook

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import react.query.useQuery
import uk.dioxic.muon.Routes
import uk.dioxic.muon.api.Api
import uk.dioxic.muon.common.QueryKey
import uk.dioxic.muon.config.Settings
import uk.dioxic.muon.utils.defaultQueryOptions

fun useSettingsFetch() =
    useQuery(
        queryKey = QueryKey.SETTINGS,
        queryFn = { readSettings() },
        options = defaultQueryOptions(QueryKey.SETTINGS)
    )

private fun readSettings() =
    MainScope().promise {
        Api.get<Settings>(Routes.settings)
    }


