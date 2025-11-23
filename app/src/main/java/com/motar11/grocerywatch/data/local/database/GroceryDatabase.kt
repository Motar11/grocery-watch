package com.motar11.grocerywatch.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.motar11.grocerywatch.data.local.dao.CategoryDao
import com.motar11.grocerywatch.data.local.dao.GroceryItemDao
import com.motar11.grocerywatch.data.local.dao.GroceryListDao
import com.motar11.grocerywatch.data.local.dao.PriceHistoryDao
import com.motar11.grocerywatch.data.local.entity.CategoryEntity
import com.motar11.grocerywatch.data.local.entity.GroceryItemEntity
import com.motar11.grocerywatch.data.local.entity.GroceryListEntity
import com.motar11.grocerywatch.data.local.entity.PriceHistoryEntryEntity

@Database(
    entities = [
        GroceryListEntity::class,
        GroceryItemEntity::class,
        PriceHistoryEntryEntity::class,
        CategoryEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class GroceryDatabase : RoomDatabase() {
    abstract fun groceryListDao(): GroceryListDao
    abstract fun groceryItemDao(): GroceryItemDao
    abstract fun priceHistoryDao(): PriceHistoryDao
    abstract fun categoryDao(): CategoryDao
}
