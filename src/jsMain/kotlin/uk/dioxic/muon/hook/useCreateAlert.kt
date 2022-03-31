package uk.dioxic.muon.hook

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import uk.dioxic.muon.api.Api
import uk.dioxic.muon.context.AlertMessage
import uk.dioxic.muon.model.SettingsLoadResponse
import uk.dioxic.muon.route.Routes

typealias CreateAlert = (AlertMessage) -> Unit

//fun useCreateAlert(): CreateAlert =
//    useQuery(
//        queryKey = QueryKey.SETTINGS.name,
//        queryFn = { readSettings() },
//        options = jso {
//            refetchOnWindowFocus = false
//            staleTime = JsDuration.MAX_VALUE
//        }
//    )

private fun readSettings() =
    MainScope().promise {
        Api.get<SettingsLoadResponse>(Routes.settings).settings
    }


