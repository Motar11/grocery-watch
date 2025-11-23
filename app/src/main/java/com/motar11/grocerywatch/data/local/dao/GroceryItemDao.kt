package com.motar11.grocerywatch.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.motar11.grocerywatch.data.local.entity.GroceryItemEntity
import com.motar11.grocerywatch.data.local.entity.GroceryItemWithPrices
import kotlinx.coroutines.flow.Flow

@Dao
interface GroceryItemDao {
    @Transaction
    @Query("SELECT * FROM grocery_items WHERE listId = :listId ORDER BY completed ASC, name")
    fun observeItemsWithPrices(listId: Long): Flow<List<GroceryItemWithPrices>>

    @Transaction
    @Query("SELECT * FROM grocery_items WHERE id = :itemId")
    fun observeItemWithPrices(itemId: Long): Flow<GroceryItemWithPrices?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: GroceryItemEntity): Long

    @Delete
    suspend fun delete(item: GroceryItemEntity)

    @Query("UPDATE grocery_items SET completed = :completed WHERE id = :itemId")
    suspend fun updateCompleted(itemId: Long, completed: Boolean)

    @Query("DELETE FROM grocery_items WHERE id = :itemId")
    suspend fun deleteById(itemId: Long)

    @Query("SELECT * FROM grocery_items")
    suspend fun getAll(): List<GroceryItemEntity>
}
