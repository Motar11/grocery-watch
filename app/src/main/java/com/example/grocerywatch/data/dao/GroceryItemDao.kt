package com.example.grocerywatch.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.grocerywatch.data.entity.GroceryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface GroceryItemDao {
    @Query("SELECT * FROM grocery_items WHERE listId = :listId ORDER BY completed ASC, name ASC")
    fun getItemsForList(listId: Long): Flow<List<GroceryItem>>

    @Query("SELECT * FROM grocery_items")
    fun getAllItems(): Flow<List<GroceryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: GroceryItem): Long

    @Update
    suspend fun update(item: GroceryItem)

    @Delete
    suspend fun delete(item: GroceryItem)

    @Query("UPDATE grocery_items SET completed = :completed WHERE id = :itemId")
    suspend fun updateCompletion(itemId: Long, completed: Boolean)
}
