package com.example.grocerywatch.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.grocerywatch.data.entity.PriceHistoryEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceHistoryDao {
    @Query("SELECT * FROM price_history WHERE itemId = :itemId ORDER BY date ASC")
    fun getHistoryForItem(itemId: Long): Flow<List<PriceHistoryEntry>>

    @Query("SELECT * FROM price_history ORDER BY date DESC")
    fun getAllEntries(): Flow<List<PriceHistoryEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: PriceHistoryEntry): Long

    @Update
    suspend fun update(entry: PriceHistoryEntry)

    @Delete
    suspend fun delete(entry: PriceHistoryEntry)

    @Query("SELECT * FROM price_history WHERE store LIKE '%' || :store || '%' ORDER BY date DESC")
    fun getEntriesForStore(store: String): Flow<List<PriceHistoryEntry>>
}
