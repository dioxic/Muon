package uk.dioxic.muon

import com.ccfraser.muirwik.components.Colors
import com.ccfraser.muirwik.components.mCssBaseline
import com.ccfraser.muirwik.components.mThemeProvider
import com.ccfraser.muirwik.components.styles.ThemeOptions
import com.ccfraser.muirwik.components.styles.createMuiTheme
import react.*
import react.dom.*
import kotlinext.js.*
import kotlinx.html.js.*
import kotlinx.coroutines.*

private val scope = MainScope()

val App = functionalComponent<RProps> {
//    val (shoppingList, setShoppingList) = useState(emptyList<ShoppingListItem>())
    val (themeColor, setThemeColor) = useState("light")

//    useEffect(dependencies = listOf()) {
//        scope.launch {
//            setShoppingList(getShoppingList())
//        }
//    }

    mCssBaseline()

    @Suppress("UnsafeCastFromDynamic")
    val themeOptions: ThemeOptions = js("({palette: { type: 'placeholder', primary: {main: 'placeholder'}}})")
    themeOptions.palette?.type = themeColor
    themeOptions.palette?.primary.main = Colors.Blue.shade500.toString()

    mThemeProvider(createMuiTheme(themeOptions)) {
        child(AppBar)
        child(MusicTable)
//        child(ShoppingList)
//        mainFrame("Intro") { setThemeColor(if (themeColor == "dark") "light" else "dark") }
    }

//    h1 {
//        +"Full-Stack Shopping List"
//    }
//    ul {
//        shoppingList.sortedByDescending(ShoppingListItem::priority).forEach { item ->
//            li {
//                key = item.toString()
//                +"[${item.priority}] ${item.desc} "
//                attrs.onClickFunction = {
//                    scope.launch {
//                        deleteShoppingListItem(item)
//                        setShoppingList(getShoppingList())
//                    }
//                }
//            }
//        }
//    }
//    child(
//        InputComponent,
//        props = jsObject {
//            onSubmit = { input ->
//                val cartItem = ShoppingListItem(input.replace("!", ""), input.count { it == '!' })
//                scope.launch {
//                    addShoppingListItem(cartItem)
//                    setShoppingList(getShoppingList())
//                }
//            }
//        }
//    )

}