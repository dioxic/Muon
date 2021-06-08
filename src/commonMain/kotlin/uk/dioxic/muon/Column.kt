package uk.dioxic.muon

import kotlinx.serialization.Serializable

@Serializable
data class Column(
    val rightAligned: Boolean = false,
    val disablePadding: Boolean = false,
    val label: String,
    val visible: Boolean
)

fun <K : Any, V : Any> Map<K, V>.orderUp(key: K) = this.order(key, -1)
fun <K : Any, V : Any> Map<K, V>.orderDown(key: K) = this.order(key, 1)

private fun <K : Any, V : Any> Map<K, V>.order(key: K, move: Int) =
    if (this.contains(key)) {
        this.toList().let { l ->
            val idx = l.indexOfFirst { it.first == key }
            l.swap(idx, idx + move).toMap()
        }
    } else {
        this
    }
