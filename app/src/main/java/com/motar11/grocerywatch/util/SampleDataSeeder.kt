package com.motar11.grocerywatch.util

import com.motar11.grocerywatch.data.local.dao.CategoryDao
import com.motar11.grocerywatch.data.local.dao.GroceryItemDao
import com.motar11.grocerywatch.data.local.dao.GroceryListDao
import com.motar11.grocerywatch.data.local.dao.PriceHistoryDao
import com.motar11.grocerywatch.data.local.entity.CategoryEntity
import com.motar11.grocerywatch.data.local.entity.GroceryItemEntity
import com.motar11.grocerywatch.data.local.entity.GroceryListEntity
import com.motar11.grocerywatch.data.local.entity.PriceHistoryEntryEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SampleDataSeeder @Inject constructor(
    private val listDao: GroceryListDao,
    private val itemDao: GroceryItemDao,
    private val priceDao: PriceHistoryDao,
    private val categoryDao: CategoryDao
) {
    suspend fun seed() {
        val existingLists = listDao.getAll()
        if (existingLists.isNotEmpty()) return

        val categories = listOf("Produce", "Bakery", "Pantry", "Dairy")
        categories.forEach { categoryDao.insert(CategoryEntity(name = it)) }

        val weekendListId = listDao.upsert(GroceryListEntity(name = "Weekend Brunch"))
        val weeklyListId = listDao.upsert(GroceryListEntity(name = "Weekly Staples"))

        val eggsId = itemDao.upsert(
            GroceryItemEntity(
                listId = weekendListId,
                name = "Free-range eggs",
                category = "Dairy",
                quantity = 2,
                notes = "Large carton"
            )
        )
        val avocadoId = itemDao.upsert(
            GroceryItemEntity(
                listId = weekendListId,
                name = "Avocados",
                category = "Produce",
                quantity = 4,
                notes = "Ready-to-eat"
            )
        )
        val breadId = itemDao.upsert(
            GroceryItemEntity(
                listId = weeklyListId,
                name = "Sourdough bread",
                category = "Bakery",
                quantity = 1,
                notes = "Slice thick"
            )
        )

        val now = System.currentTimeMillis()
        priceDao.insert(PriceHistoryEntryEntity(itemId = eggsId, price = 3.99, store = "GreenMart", date = now - 1000L * 60 * 60 * 24 * 7))
        priceDao.insert(PriceHistoryEntryEntity(itemId = eggsId, price = 4.49, store = "FreshFoods", date = now - 1000L * 60 * 60 * 24 * 3))
        priceDao.insert(PriceHistoryEntryEntity(itemId = avocadoId, price = 1.29, store = "GreenMart", date = now - 1000L * 60 * 60 * 24 * 10))
        priceDao.insert(PriceHistoryEntryEntity(itemId = avocadoId, price = 1.49, store = "City Market", date = now - 1000L * 60 * 60 * 24 * 2))
        priceDao.insert(PriceHistoryEntryEntity(itemId = breadId, price = 5.5, store = "Bakery Lane", date = now - 1000L * 60 * 60 * 24 * 5))
        priceDao.insert(PriceHistoryEntryEntity(itemId = breadId, price = 4.95, store = "FreshFoods", date = now - 1000L * 60 * 60 * 24 * 1))
    }
}
