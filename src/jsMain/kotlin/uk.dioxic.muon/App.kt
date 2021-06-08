package uk.dioxic.muon

import com.ccfraser.muirwik.components.mCircularProgress
import com.ccfraser.muirwik.components.mCssBaseline
import com.ccfraser.muirwik.components.mThemeProvider
import com.ccfraser.muirwik.components.styles.ThemeOptions
import com.ccfraser.muirwik.components.styles.createMuiTheme
import com.ccfraser.muirwik.components.Colors
import react.*
import kotlinext.js.*
import kotlinx.coroutines.*
import uk.dioxic.muon.api.fetchFullConfig
import uk.dioxic.muon.component.MainFrame
import uk.dioxic.muon.component.MainFrameProps

private val scope = MainScope()

val App = functionalComponent<RProps> {
    val (isLoading, setLoading) = useState(true)
    val (themeColor, setThemeColor) = useState("light")
    val (config, setConfig) = useState(Config.Default)
//    val (searchText, setSearchText) = useState("")
    mCssBaseline()

    val themeOptions: ThemeOptions = jsObject {
        palette = jsObject {
            type = themeColor
            primary = jsObject {
                main = Colors.Blue.shade500.toString()
            }
        }
    }

    if (isLoading) {
        mCircularProgress()
        scope.launch {
            if (config == Config.Default) {
                setConfig(fetchFullConfig())
            }
        }.invokeOnCompletion {
            setLoading(false)
        }
    } else {
//        ConfigContext.Provider(config) {
        mThemeProvider(createMuiTheme(themeOptions)) {
            child(MainFrame, props = jsObject {
                initialView = "Import"
                onThemeSwitch = {
                    setThemeColor(if (themeColor == "dark") "light" else "dark")
                }
            })
        }
//        mThemeProvider(createMuiTheme(themeOptions)) {
//            mainFrame(Page.Intro) { setThemeColor(if (themeColor == "dark") "light" else "dark") }
//        }
//        child(AppBar, props = jsObject {
//            onSearchSubmit = { setSearchText(it) }
//        })
//        child(MusicTable, props = jsObject {
//            filter = searchText
//        })
//        child(ShoppingList)
//        mainFrame("Intro") { setThemeColor(if (themeColor == "dark") "light" else "dark") }
//        }
    }
}
