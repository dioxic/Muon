package uk.dioxic.muon.repository

import uk.dioxic.muon.ShoppingListItem

class ShoppingRepository {

    private var shoppingList: MutableList<ShoppingListItem> = mutableListOf()

    init {
        shoppingList.addAll(
            listOf(
                ShoppingListItem("Cucumbers 🥒", 1),
                ShoppingListItem("Tomatoes 🍅", 2),
                ShoppingListItem("Orange Juice 🍊", 3)
            )
        )
    }

    fun get(): List<ShoppingListItem> = shoppingList.toList()

    fun add(item: ShoppingListItem) = shoppingList.add(item)

    fun delete(id: Int) = shoppingList.removeIf { it.id == id }

}