package uk.dioxic.muon

import com.ccfraser.muirwik.components.Colors
import com.ccfraser.muirwik.components.mCssBaseline
import com.ccfraser.muirwik.components.mThemeProvider
import com.ccfraser.muirwik.components.styles.ThemeOptions
import com.ccfraser.muirwik.components.styles.createMuiTheme
import react.*
import kotlinext.js.*
import kotlinx.coroutines.*

private val scope = MainScope()

val App = functionalComponent<RProps> {
    val (themeColor, setThemeColor) = useState("light")
//    val (searchText, setSearchText) = useState("")

    mCssBaseline()

    @Suppress("UnsafeCastFromDynamic")
    val themeOptions: ThemeOptions = js("({palette: { type: 'placeholder', primary: {main: 'placeholder'}}})")
    themeOptions.palette?.type = themeColor
    themeOptions.palette?.primary.main = Colors.Blue.shade500.toString()

    mThemeProvider(createMuiTheme(themeOptions)) {
        child(MainFrame, props = jsObject {
            initialView = "Import"
            onThemeSwitch = {
                setThemeColor(if (themeColor == "dark") "light" else "dark")
            }
        })
//        child(AppBar, props = jsObject {
//            onSearchSubmit = { setSearchText(it) }
//        })
//        child(MusicTable, props = jsObject {
//            filter = searchText
//        })
//        child(ShoppingList)
//        mainFrame("Intro") { setThemeColor(if (themeColor == "dark") "light" else "dark") }
    }

}