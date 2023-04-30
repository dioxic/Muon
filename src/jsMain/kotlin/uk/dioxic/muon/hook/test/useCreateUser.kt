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

typealias CreateUser = (User) -> Unit

fun useCreateUser(): CreateUser {
    val client = useQueryClient()
    return useMutation<User, Error, User, QueryKey>(
        mutationFn = { user -> createUser(user) },
        options = jso {
            onSuccess = { _, _, _ -> client.invalidateQueries<Void>(USERS_QUERY_KEY) }
        }
    ).mutate.unsafeCast<CreateUser>()
}

private fun createUser(user: User): Promise<User> =
    window.fetch(
        input = "https://jsonplaceholder.typicode.com/users",
        init = jso {
            method = "POST"
            body = JSON.stringify(user)
        }
    ).then { it.json() }.then { it.unsafeCast<User>() }
