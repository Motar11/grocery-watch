package com.motar11.grocerywatch.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.motar11.grocerywatch.data.local.entity.GroceryListEntity
import com.motar11.grocerywatch.data.local.entity.GroceryListWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface GroceryListDao {
    @Transaction
    @Query("SELECT * FROM grocery_lists ORDER BY createdAt DESC")
    fun observeListsWithItems(): Flow<List<GroceryListWithItems>>

    @Query("SELECT * FROM grocery_lists ORDER BY createdAt DESC")
    fun observeLists(): Flow<List<GroceryListEntity>>

    @Query("SELECT * FROM grocery_lists ORDER BY createdAt DESC")
    suspend fun getAll(): List<GroceryListEntity>

    @Query("SELECT * FROM grocery_lists WHERE id = :id")
    fun observeList(id: Long): Flow<GroceryListEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(list: GroceryListEntity): Long

    @Delete
    suspend fun delete(list: GroceryListEntity)

    @Query("DELETE FROM grocery_lists WHERE id = :id")
    suspend fun deleteById(id: Long)
}
