package com.example.grocerywatch.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "price_history",
    foreignKeys = [
        ForeignKey(
            entity = GroceryItem::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["itemId"])]
)
data class PriceHistoryEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemId: Long,
    val price: Double,
    val store: String,
    val date: Long
)
