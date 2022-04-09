package uk.dioxic.muon.component.table.actions

import kotlinx.js.ReadonlyArray

fun <D : Any> actions(
    block: ActionBuilder<D>.() -> Unit,
): ReadonlyArray<Action<D>> =
    ActionBuilder<D>().apply(block).build()

class ActionBuilder<D : Any> {
    private val actions = mutableListOf<Action<D>>()

    fun action(block: Action<D>.() -> Unit) {
        val action = Action<D>().apply(block)
        actions.add(action)
    }

    fun build(): ReadonlyArray<Action<D>> =
        actions.toTypedArray()

}

//external interface Action<D: Any> {
//    var displayName: String
//    var onClick: (D) -> Unit
//}

class Action<D : Any>(
    var displayName: String = "",
    var onClick: (D) -> Unit = { }
)