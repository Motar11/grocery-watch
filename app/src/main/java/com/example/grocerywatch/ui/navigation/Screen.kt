package com.example.grocerywatch.ui.navigation

sealed class Screen(val route: String) {
    data object Lists : Screen("lists")
    data object Detail : Screen("detail/{listId}") {
        fun create(listId: Long) = "detail/$listId"
    }
    data object PriceHistory : Screen("history/{itemId}") {
        fun create(itemId: Long) = "history/$itemId"
    }
    data object Compare : Screen("compare")
}
