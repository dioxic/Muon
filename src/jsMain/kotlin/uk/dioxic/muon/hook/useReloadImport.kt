package uk.dioxic.muon.hook

import react.query.useQueryClient
import uk.dioxic.muon.QueryKey

typealias ReloadImport = () -> Unit

fun useReloadImport(): ReloadImport {
    val queryClient = useQueryClient()
    return {
        queryClient.invalidateQueries<Nothing>(QueryKey.IMPORT.name)
    }
}
