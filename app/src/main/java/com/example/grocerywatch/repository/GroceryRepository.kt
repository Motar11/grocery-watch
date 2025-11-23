package com.example.grocerywatch.repository

import com.example.grocerywatch.data.dao.GroceryItemDao
import com.example.grocerywatch.data.dao.GroceryListDao
import com.example.grocerywatch.data.dao.PriceHistoryDao
import com.example.grocerywatch.data.entity.GroceryItem
import com.example.grocerywatch.data.entity.GroceryList
import com.example.grocerywatch.data.entity.PriceHistoryEntry
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@Singleton
class GroceryRepository @Inject constructor(
    private val listDao: GroceryListDao,
    private val itemDao: GroceryItemDao,
    private val priceHistoryDao: PriceHistoryDao
) {

    val groceryLists: Flow<List<GroceryList>> = listDao.getLists()

    fun getItemsForList(listId: Long): Flow<List<GroceryItem>> = itemDao.getItemsForList(listId)

    fun getPriceHistoryForItem(itemId: Long): Flow<List<PriceHistoryEntry>> = priceHistoryDao.getHistoryForItem(itemId)

    suspend fun addList(name: String) {
        listDao.insert(GroceryList(name = name))
    }

    suspend fun updateList(list: GroceryList) = listDao.update(list)

    suspend fun deleteList(list: GroceryList) = listDao.delete(list)

    suspend fun addItem(
        listId: Long,
        name: String,
        category: String,
        quantity: Int,
        notes: String
    ) {
        itemDao.insert(
            GroceryItem(
                listId = listId,
                name = name,
                category = category,
                quantity = quantity,
                notes = notes
            )
        )
    }

    suspend fun updateItem(item: GroceryItem) = itemDao.update(item)

    suspend fun toggleComplete(itemId: Long, completed: Boolean) = itemDao.updateCompletion(itemId, completed)

    suspend fun deleteItem(item: GroceryItem) = itemDao.delete(item)

    suspend fun addPriceEntry(itemId: Long, price: Double, store: String, date: Long) {
        priceHistoryDao.insert(
            PriceHistoryEntry(
                itemId = itemId,
                price = price,
                store = store,
                date = date
            )
        )
    }

    suspend fun updatePriceEntry(entry: PriceHistoryEntry) = priceHistoryDao.update(entry)

    fun getLowestPrices(storeFilter: String?): Flow<List<PriceComparison>> {
        return combine(
            itemDao.getAllItems(),
            if (storeFilter.isNullOrBlank()) priceHistoryDao.getAllEntries() else priceHistoryDao.getEntriesForStore(storeFilter)
        ) { items, entries ->
            val grouped = entries.groupBy { it.itemId }
            items.mapNotNull { item ->
                grouped[item.id]?.minByOrNull { it.price }?.let { entry ->
                    PriceComparison(
                        itemId = item.id,
                        itemName = item.name,
                        store = entry.store,
                        price = entry.price
                    )
                }
            }
        }
    }

    suspend fun exportPriceHistoryCsv(): String = withContext(Dispatchers.IO) {
        val lists = groceryLists.first()
        val items = itemDao.getAllItems().first()
        val history = priceHistoryDao.getAllEntries().first()
        val listLookup = lists.associateBy { it.id }
        val itemLookup = items.associateBy { it.id }
        buildString {
            appendLine("list,item,store,price,date")
            history.forEach { entry ->
                val item = itemLookup[entry.itemId]
                val list = item?.let { listLookup[it.listId] }
                appendLine("${list?.name ?: ""},${item?.name ?: ""},${entry.store},${entry.price},${entry.date}")
            }
        }
    }
}

data class PriceComparison(
    val itemId: Long,
    val itemName: String,
    val store: String,
    val price: Double
)
