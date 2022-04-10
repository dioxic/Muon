package uk.dioxic.muon.hook

import react.query.useQueryClient
import uk.dioxic.muon.common.QueryKey

typealias ReloadImport = () -> Unit

fun useImportReload(): ReloadImport {
    val queryClient = useQueryClient()
    return {
        queryClient.invalidateQueries<Nothing>(QueryKey.IMPORT)
    }
}
