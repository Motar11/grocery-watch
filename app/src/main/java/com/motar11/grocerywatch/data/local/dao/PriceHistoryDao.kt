package com.motar11.grocerywatch.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.motar11.grocerywatch.data.local.entity.PriceHistoryEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: PriceHistoryEntryEntity): Long

    @Query("SELECT * FROM price_history WHERE itemId = :itemId ORDER BY date DESC")
    fun observeForItem(itemId: Long): Flow<List<PriceHistoryEntryEntity>>

    @Query("SELECT * FROM price_history")
    suspend fun getAll(): List<PriceHistoryEntryEntity>
}
