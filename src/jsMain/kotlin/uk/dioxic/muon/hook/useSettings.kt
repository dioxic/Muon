package uk.dioxic.muon.hook

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import react.query.useQuery
import uk.dioxic.muon.QueryKey
import uk.dioxic.muon.api.Api
import uk.dioxic.muon.model.SettingsLoadResponse
import uk.dioxic.muon.route.Routes
import uk.dioxic.muon.utils.defaultQueryOptions

fun useSettings() =
    useQuery(
        queryKey = QueryKey.SETTINGS.name,
        queryFn = { readSettings() },
        options = defaultQueryOptions(QueryKey.SETTINGS)
    )

private fun readSettings() =
    MainScope().promise {
        Api.get<SettingsLoadResponse>(Routes.settings).settings
    }


