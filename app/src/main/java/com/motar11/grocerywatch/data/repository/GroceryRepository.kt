package com.motar11.grocerywatch.data.repository

import com.motar11.grocerywatch.data.local.entity.CategoryEntity
import com.motar11.grocerywatch.data.local.entity.GroceryItemEntity
import com.motar11.grocerywatch.data.local.entity.GroceryItemWithPrices
import com.motar11.grocerywatch.data.local.entity.GroceryListEntity
import com.motar11.grocerywatch.data.local.entity.GroceryListWithItems
import com.motar11.grocerywatch.data.local.entity.PriceHistoryEntryEntity
import kotlinx.coroutines.flow.Flow

interface GroceryRepository {
    fun observeLists(): Flow<List<GroceryListWithItems>>
    fun observeList(listId: Long): Flow<GroceryListEntity?>
    fun observeItems(listId: Long): Flow<List<GroceryItemWithPrices>>
    fun observeItem(itemId: Long): Flow<GroceryItemWithPrices?>
    fun observePriceHistory(itemId: Long): Flow<List<PriceHistoryEntryEntity>>
    fun observeCategories(): Flow<List<CategoryEntity>>
    fun observeLowestPrices(storeFilter: String? = null): Flow<List<PriceSnapshot>>

    suspend fun addList(name: String): Long
    suspend fun updateList(list: GroceryListEntity)
    suspend fun deleteList(id: Long)

    suspend fun addOrUpdateItem(item: GroceryItemEntity): Long
    suspend fun toggleCompleted(itemId: Long, completed: Boolean)
    suspend fun deleteItem(itemId: Long)

    suspend fun addPriceEntry(itemId: Long, price: Double, store: String, date: Long = System.currentTimeMillis())
    suspend fun ensureCategory(name: String)

    suspend fun exportCsv(): String
    suspend fun importCsv(csv: String)
}
