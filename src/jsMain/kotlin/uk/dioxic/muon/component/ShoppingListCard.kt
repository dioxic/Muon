package uk.dioxic.muon.component

import com.ccfraser.muirwik.components.MTypographyVariant
import com.ccfraser.muirwik.components.button.mIconButton
import com.ccfraser.muirwik.components.card.mCard
import com.ccfraser.muirwik.components.card.mCardContent
import com.ccfraser.muirwik.components.card.mCardMedia
import com.ccfraser.muirwik.components.list.mList
import com.ccfraser.muirwik.components.list.mListItem
import com.ccfraser.muirwik.components.mTypography
import com.ccfraser.muirwik.components.spacingUnits
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.css.*
import react.*
import styled.StyleSheet
import styled.css
import styled.styledDiv
import uk.dioxic.muon.ShoppingListItem
import uk.dioxic.muon.api.getShoppingList

private object ComponentStyles : StyleSheet("ComponentStyles", isStatic = true) {
    val listDiv by css {
        display = Display.inlineFlex
        padding(1.spacingUnits)
    }

    val inline by css {
        display = Display.inlineBlock
    }
}

private val scope = MainScope()

val ShoppingList = fc<Props> {
    val (shoppingList, setShoppingList) = useState(emptyList<ShoppingListItem>())

    useEffectOnce {
        scope.launch {
            setShoppingList(getShoppingList())
        }
    }

    styledDiv {
        css {
            padding(16.px)
        }
        mCard {
            css { display = Display.flex }
            styledDiv {
                css {
                    display = Display.flex
                    flexDirection = FlexDirection.column
                    flexGrow = 1.0
                }
                mCardContent {
                    css { flex(1.0, 0.0, FlexBasis.auto) }
                    mTypography("Shopping List", variant = MTypographyVariant.h1)
                    mList {
//                        css(ComponentStyles.listDiv)
//                        mListItem(
//                            primaryText = "hello",
//                            selected = (selected == i),
//                            key = i.toString(),
//                            onClick = { setState { selected = i}}
//                        )
                        shoppingList.sortedByDescending(ShoppingListItem::priority).forEach { item ->
                            mListItem(
                                primaryText = item.desc,
                                key = i.toString(),
                            )
                        }

                    }
//                    mTypography(
//                        "Mac Miller",
//                        variant = MTypographyVariant.subtitle1,
//                        color = MTypographyColor.textSecondary
//                    )
                }
                styledDiv {
                    css {
                        display = Display.flex
                        alignItems = Align.center
                        paddingLeft = 1.spacingUnits
                        paddingBottom = 1.spacingUnits
                    }
                    mIconButton("skip_previous")

                    // Demo was using an svg icon, so could easily change the size...we will keep it the same for now
                    mIconButton("play_arrow")

                    mIconButton("skip_next")
                }
            }
            mCardMedia("/images/cards/live-from-space.jpg", "Live from space album cover") {
                css { css { height = 151.px; width = 151.px } }
            }
        }
    }
}