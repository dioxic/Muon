package uk.dioxic.muon

import com.ccfraser.muirwik.components.styles.mStylesProvider
import kotlinx.browser.document
import react.dom.render

fun main() {
    render(document.getElementById("root")) {
        mStylesProvider("jss-insertion-point") {
            child(App)
        }
    }
}