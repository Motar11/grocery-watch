package com.example.grocerywatch.util

import com.example.grocerywatch.data.entity.GroceryItem
import com.example.grocerywatch.data.entity.GroceryList
import com.example.grocerywatch.data.entity.PriceHistoryEntry

object SampleData {
    val lists = listOf(
        GroceryList(id = 1, name = "Weekly Essentials"),
        GroceryList(id = 2, name = "BBQ Party"),
        GroceryList(id = 3, name = "Meal Prep")
    )

    val items = listOf(
        GroceryItem(id = 1, listId = 1, name = "Milk", category = "Dairy", quantity = 2, notes = "2% fat"),
        GroceryItem(id = 2, listId = 1, name = "Bread", category = "Bakery", quantity = 1),
        GroceryItem(id = 3, listId = 2, name = "Steak", category = "Meat", quantity = 4, notes = "Ribeye"),
        GroceryItem(id = 4, listId = 3, name = "Rice", category = "Grains", quantity = 1)
    )

    val history = listOf(
        PriceHistoryEntry(id = 1, itemId = 1, price = 3.49, store = "Fresh Mart", date = System.currentTimeMillis() - 86_400_000L * 7),
        PriceHistoryEntry(id = 2, itemId = 1, price = 3.69, store = "Fresh Mart", date = System.currentTimeMillis()),
        PriceHistoryEntry(id = 3, itemId = 2, price = 2.49, store = "Corner Bakery", date = System.currentTimeMillis()),
        PriceHistoryEntry(id = 4, itemId = 3, price = 9.99, store = "Butcher Block", date = System.currentTimeMillis() - 86_400_000L * 14),
        PriceHistoryEntry(id = 5, itemId = 3, price = 10.49, store = "Butcher Block", date = System.currentTimeMillis()),
        PriceHistoryEntry(id = 6, itemId = 4, price = 12.00, store = "Bulk Foods", date = System.currentTimeMillis())
    )
}
