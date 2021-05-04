package uk.dioxic.muon

fun Int.toTimeString(): String {
    val minutes = this.floorDiv(60)
    val seconds = this.mod(60).toString().padStart(2, '0')
    return "${minutes}m ${seconds}s"
}

fun <t> List<t>.swap(a: Int, b: Int): List<t> = this
    .toMutableList()
    .also {
        it[a] = this[b]
        it[b] = this[a]
    }

//fun <t> MutableList<t>.swap(a: Int, b: Int): List<t> = this
//    .also {
//        it[a] = this[b]
//        it[b] = this[a]
//    }