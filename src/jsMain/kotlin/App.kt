import react.*
import react.dom.*
import kotlinext.js.*
import kotlinx.html.js.*
import kotlinx.coroutines.*
import kotlinx.css.*
import styled.css
import styled.styledDiv
import uk.dioxic.muon.ShoppingListItem

private val scope = MainScope()

val App = functionalComponent<RProps> {
    val (shoppingList, setShoppingList) = useState(emptyList<ShoppingListItem>())

    useEffect(dependencies = listOf()) {
        scope.launch {
            setShoppingList(getShoppingList())
        }
    }

    styledDiv {
        css {
            position = Position.absolute
            alignContent = Align.center
            top = 10.px
            right = 10.px
        }
        h1 {
            +"Full-Stack Shopping List"
        }
        ul {
            shoppingList.sortedByDescending(ShoppingListItem::priority).forEach { item ->
                li {
                    key = item.toString()
                    +"[${item.priority}] ${item.desc} "
                    attrs.onClickFunction = {
                        scope.launch {
                            deleteShoppingListItem(item)
                            setShoppingList(getShoppingList())
                        }
                    }
                }
            }
        }
        child(
            InputComponent,
            props = jsObject {
                onSubmit = { input ->
                    val cartItem = ShoppingListItem(input.replace("!", ""), input.count { it == '!' })
                    scope.launch {
                        addShoppingListItem(cartItem)
                        setShoppingList(getShoppingList())
                    }
                }
            }
        )
    }
}