package uk.dioxic.muon.hook.test

import js.core.Void
import js.core.jso
import kotlinx.browser.window
import tanstack.query.core.QueryKey
import tanstack.react.query.useMutation
import tanstack.react.query.useQueryClient
import uk.dioxic.muon.common.QueryKeys.USERS_QUERY_KEY
import uk.dioxic.muon.entity.User
import kotlin.js.Promise

typealias DeleteUser = (User) -> Unit

fun useDeleteUser(): DeleteUser {
    val client = useQueryClient()
    return useMutation<Unit, Error, User, QueryKey>(
        mutationFn = { user -> deleteUser(user) },
        options = jso {
            onSuccess = { _, _, _ -> client.invalidateQueries<Void>(USERS_QUERY_KEY) }
        }
    ).mutate.unsafeCast<DeleteUser>()
}

private fun deleteUser(user: User): Promise<Unit> =
    window.fetch(
        input = "https://jsonplaceholder.typicode.com/users/${user.id}",
        init = jso { method = "DELETE" }
    ).then {}
