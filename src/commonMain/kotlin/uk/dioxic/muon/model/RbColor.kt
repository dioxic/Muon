package uk.dioxic.muon.model

enum class RbColor {
    PINK,
    RED,
    ORANGE,
    YELLOW,
    GREEN,
    AQUA,
    BLUE,
    PURPLE,
}
fun RbColor.toHex() =
    when(this) {
        RbColor.PINK -> "#ee67dd"
        RbColor.RED -> "#e6261f"
        RbColor.ORANGE -> "#eb7532"
        RbColor.YELLOW -> "#f7d038"
        RbColor.GREEN -> "#85e048"
        RbColor.AQUA -> "#3dc8f4"
        RbColor.BLUE -> "#3554f4"
        RbColor.PURPLE -> "#a043ea"
    }