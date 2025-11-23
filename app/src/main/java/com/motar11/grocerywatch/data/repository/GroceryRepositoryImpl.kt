package com.motar11.grocerywatch.data.repository

import com.motar11.grocerywatch.data.local.dao.CategoryDao
import com.motar11.grocerywatch.data.local.dao.GroceryItemDao
import com.motar11.grocerywatch.data.local.dao.GroceryListDao
import com.motar11.grocerywatch.data.local.dao.PriceHistoryDao
import com.motar11.grocerywatch.data.local.entity.CategoryEntity
import com.motar11.grocerywatch.data.local.entity.GroceryItemEntity
import com.motar11.grocerywatch.data.local.entity.GroceryItemWithPrices
import com.motar11.grocerywatch.data.local.entity.GroceryListEntity
import com.motar11.grocerywatch.data.local.entity.GroceryListWithItems
import com.motar11.grocerywatch.data.local.entity.PriceHistoryEntryEntity
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class GroceryRepositoryImpl @Inject constructor(
    private val listDao: GroceryListDao,
    private val itemDao: GroceryItemDao,
    private val priceDao: PriceHistoryDao,
    private val categoryDao: CategoryDao
) : GroceryRepository {

    override fun observeLists(): Flow<List<GroceryListWithItems>> =
        listDao.observeListsWithItems()

    override fun observeList(listId: Long): Flow<GroceryListEntity?> =
        listDao.observeList(listId)

    override fun observeItems(listId: Long): Flow<List<GroceryItemWithPrices>> =
        itemDao.observeItemsWithPrices(listId)

    override fun observeItem(itemId: Long): Flow<GroceryItemWithPrices?> =
        itemDao.observeItemWithPrices(itemId)

    override fun observePriceHistory(itemId: Long): Flow<List<PriceHistoryEntryEntity>> =
        priceDao.observeForItem(itemId)

    override fun observeCategories(): Flow<List<CategoryEntity>> = categoryDao.observeCategories()

    override fun observeLowestPrices(storeFilter: String?): Flow<List<PriceSnapshot>> =
        listDao.observeListsWithItems().map { lists ->
            lists.flatMap { listWithItems ->
                listWithItems.items.mapNotNull { itemWithPrices ->
                    val filtered = itemWithPrices.priceHistory.filter { entry ->
                        storeFilter.isNullOrBlank() || entry.store.equals(storeFilter, ignoreCase = true)
                    }
                    val latestPerStore = filtered
                        .groupBy { it.store }
                        .mapValues { (_, entries) -> entries.maxByOrNull { it.date } }
                        .values
                        .filterNotNull()
                    val lowest = latestPerStore.minByOrNull { it.price }
                    lowest?.let {
                        PriceSnapshot(
                            itemId = itemWithPrices.item.id,
                            itemName = itemWithPrices.item.name,
                            store = it.store,
                            price = it.price,
                            listName = listWithItems.list.name,
                            lastUpdated = it.date
                        )
                    }
                }
            }.sortedBy { it.price }
        }

    override suspend fun addList(name: String): Long =
        listDao.upsert(GroceryListEntity(name = name.trim()))

    override suspend fun updateList(list: GroceryListEntity) {
        listDao.upsert(list)
    }

    override suspend fun deleteList(id: Long) {
        listDao.deleteById(id)
    }

    override suspend fun addOrUpdateItem(item: GroceryItemEntity): Long {
        ensureCategory(item.category)
        return itemDao.upsert(item.copy(name = item.name.trim()))
    }

    override suspend fun toggleCompleted(itemId: Long, completed: Boolean) {
        itemDao.updateCompleted(itemId, completed)
    }

    override suspend fun deleteItem(itemId: Long) {
        itemDao.deleteById(itemId)
    }

    override suspend fun addPriceEntry(itemId: Long, price: Double, store: String, date: Long) {
        priceDao.insert(
            PriceHistoryEntryEntity(
                itemId = itemId,
                price = price,
                store = store.trim(),
                date = date
            )
        )
    }

    override suspend fun ensureCategory(name: String) {
        if (name.isNotBlank()) {
            categoryDao.insert(CategoryEntity(name = name.trim()))
        }
    }

    override suspend fun exportCsv(): String {
        val lists = listDao.getAll()
        val items = itemDao.getAll()
        val prices = priceDao.getAll()
        val categories = categoryDao.getAll()

        val builder = StringBuilder()
        builder.appendLine("type,listId,itemId,name,category,quantity,notes,completed,price,store,date")
        categories.forEach { category ->
            builder.appendLine("category,,,${category.name},,,,,,")
        }
        lists.forEach { list ->
            builder.appendLine("list,${list.id},,${list.name},,,,,,")
        }
        items.forEach { item ->
            builder.appendLine(
                "item,${item.listId},${item.id},${item.name},${item.category},${item.quantity},${item.notes},${item.completed},,,"
            )
        }
        prices.forEach { entry ->
            builder.appendLine(
                "price,,${entry.itemId},,,,,${entry.price},${entry.store},${entry.date}"
            )
        }
        return builder.toString()
    }

    override suspend fun importCsv(csv: String) {
        val lines = csv.lineSequence()
            .filter { it.isNotBlank() }
            .drop(1) // header
        lines.forEach { line ->
            val parts = line.split(',')
            if (parts.isEmpty()) return@forEach
            when (parts[0]) {
                "category" -> ensureCategory(parts.getOrNull(3).orEmpty())
                "list" -> {
                    val id = parts.getOrNull(1)?.toLongOrNull()
                    val name = parts.getOrNull(3).orEmpty()
                    if (name.isNotBlank()) {
                        listDao.upsert(
                            GroceryListEntity(
                                id = id ?: 0,
                                name = name
                            )
                        )
                    }
                }
                "item" -> {
                    val listId = parts.getOrNull(1)?.toLongOrNull() ?: return@forEach
                    val itemId = parts.getOrNull(2)?.toLongOrNull() ?: 0
                    val name = parts.getOrNull(3).orEmpty()
                    val category = parts.getOrNull(4).orEmpty()
                    val quantity = parts.getOrNull(5)?.toIntOrNull() ?: 1
                    val notes = parts.getOrNull(6).orEmpty()
                    val completed = parts.getOrNull(7)?.toBooleanStrictOrNull() ?: false
                    ensureCategory(category)
                    itemDao.upsert(
                        GroceryItemEntity(
                            id = itemId,
                            listId = listId,
                            name = name,
                            category = category,
                            quantity = quantity,
                            notes = notes,
                            completed = completed
                        )
                    )
                }
                "price" -> {
                    val itemId = parts.getOrNull(2)?.toLongOrNull() ?: return@forEach
                    val price = parts.getOrNull(8)?.toDoubleOrNull() ?: return@forEach
                    val store = parts.getOrNull(9).orEmpty()
                    val date = parts.getOrNull(10)?.toLongOrNull() ?: System.currentTimeMillis()
                    addPriceEntry(itemId, price, store, date)
                }
            }
        }
    }
}
