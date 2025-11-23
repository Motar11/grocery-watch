package com.example.grocerywatch.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.grocerywatch.data.entity.GroceryList
import kotlinx.coroutines.flow.Flow

@Dao
interface GroceryListDao {
    @Query("SELECT * FROM grocery_lists ORDER BY createdAt DESC")
    fun getLists(): Flow<List<GroceryList>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: GroceryList): Long

    @Update
    suspend fun update(list: GroceryList)

    @Delete
    suspend fun delete(list: GroceryList)
}
