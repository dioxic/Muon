package uk.dioxic.muon

fun Int.toTimeString(): String {
    val minutes = this.floorDiv(60)
    val seconds = this.mod(60)
    return "${minutes}m ${seconds}s"
}