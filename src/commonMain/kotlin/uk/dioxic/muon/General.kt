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

fun <T> coalesce(ignore: String, vararg items: T): T {
    items.forEach {
        if (it != null) {
            if (ignore == it) {
                // goto next item
            } else {
                return it
            }
        }
    }
    return items.first()
}

fun String.fileExtension() =
    Regex(""".(\w{3,4})$""")
        .find(this)
        ?.groupValues
        ?.get(1)

fun <T> coalesce(vararg items: T) = coalesce("", items)
