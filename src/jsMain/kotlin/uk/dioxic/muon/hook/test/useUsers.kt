package uk.dioxic.muon.hook.test

import kotlinx.browser.window
import tanstack.query.core.QueryKey
import tanstack.react.query.useQuery
import uk.dioxic.muon.common.QueryKeys.USERS_QUERY_KEY
import uk.dioxic.muon.entity.Users
import kotlin.js.Promise

fun useUsers(): Users {
    val result = useQuery<Users, Error, Users, QueryKey>(
        queryKey = USERS_QUERY_KEY,
        queryFn = { getUsers() }
    )
    return result.data ?: emptyArray()
}

private fun getUsers(): Promise<Users> =
    window.fetch("https://jsonplaceholder.typicode.com/users")
        .then { it.json() }
        .then { it.unsafeCast<Users>() }
