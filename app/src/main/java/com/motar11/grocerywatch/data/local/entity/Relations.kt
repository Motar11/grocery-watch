package com.motar11.grocerywatch.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Room relation models for combining lists, items, and price history.
 */
data class GroceryItemWithPrices(
    @Embedded val item: GroceryItemEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "itemId",
        entity = PriceHistoryEntryEntity::class
    )
    val priceHistory: List<PriceHistoryEntryEntity>
)

data class GroceryListWithItems(
    @Embedded val list: GroceryListEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "listId",
        entity = GroceryItemEntity::class
    )
    val items: List<GroceryItemWithPrices>
)
