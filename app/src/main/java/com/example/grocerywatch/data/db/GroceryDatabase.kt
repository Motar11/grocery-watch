package com.example.grocerywatch.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.grocerywatch.data.dao.GroceryItemDao
import com.example.grocerywatch.data.dao.GroceryListDao
import com.example.grocerywatch.data.dao.PriceHistoryDao
import com.example.grocerywatch.data.entity.GroceryItem
import com.example.grocerywatch.data.entity.GroceryList
import com.example.grocerywatch.data.entity.PriceHistoryEntry

@Database(
    entities = [GroceryList::class, GroceryItem::class, PriceHistoryEntry::class],
    version = 1,
    exportSchema = false
)
abstract class GroceryDatabase : RoomDatabase() {
    abstract fun groceryListDao(): GroceryListDao
    abstract fun groceryItemDao(): GroceryItemDao
    abstract fun priceHistoryDao(): PriceHistoryDao
}
