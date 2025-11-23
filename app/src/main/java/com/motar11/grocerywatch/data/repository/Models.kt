package com.motar11.grocerywatch.data.repository

data class PriceSnapshot(
    val itemId: Long,
    val itemName: String,
    val store: String,
    val price: Double,
    val listName: String,
    val lastUpdated: Long
)
