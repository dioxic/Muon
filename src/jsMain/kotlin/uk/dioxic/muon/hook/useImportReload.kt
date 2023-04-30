package uk.dioxic.muon.hook

import tanstack.react.query.useQueryClient
import uk.dioxic.muon.common.QueryKeys

typealias ReloadImport = () -> Unit

fun useImportReload(): ReloadImport {
    val queryClient = useQueryClient()
    return {
        queryClient.invalidateQueries<Nothing>(QueryKeys.IMPORT)
    }
}
