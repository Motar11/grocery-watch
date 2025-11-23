package com.motar11.grocerywatch.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motar11.grocerywatch.data.local.entity.GroceryItemEntity
import com.motar11.grocerywatch.data.local.entity.GroceryItemWithPrices
import com.motar11.grocerywatch.data.local.entity.PriceHistoryEntryEntity
import com.motar11.grocerywatch.data.repository.GroceryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.pow

data class ItemDetailState(
    val item: GroceryItemWithPrices? = null,
    val priceHistory: List<PriceHistoryEntryEntity> = emptyList(),
    val trendLine: List<Pair<Long, Double>> = emptyList()
)

@HiltViewModel
class ItemDetailViewModel @Inject constructor(
    private val repository: GroceryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val itemId: Long = savedStateHandle["itemId"] ?: -1

    val state = combine(
        repository.observeItem(itemId),
        repository.observePriceHistory(itemId)
    ) { item, history ->
        ItemDetailState(
            item = item,
            priceHistory = history,
            trendLine = buildTrendLine(history)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ItemDetailState())

    fun updateItem(
        name: String,
        category: String,
        quantity: Int,
        notes: String,
        completed: Boolean
    ) {
        val current = state.value.item?.item ?: return
        viewModelScope.launch {
            repository.addOrUpdateItem(
                GroceryItemEntity(
                    id = current.id,
                    listId = current.listId,
                    name = name,
                    category = category,
                    quantity = quantity,
                    notes = notes,
                    completed = completed,
                    createdAt = current.createdAt
                )
            )
        }
    }

    fun addPrice(price: Double, store: String, date: Long) {
        viewModelScope.launch { repository.addPriceEntry(itemId, price, store, date) }
    }

    fun toggleComplete(completed: Boolean) {
        viewModelScope.launch { repository.toggleCompleted(itemId, completed) }
    }

    private fun buildTrendLine(history: List<PriceHistoryEntryEntity>): List<Pair<Long, Double>> {
        if (history.size < 2) return emptyList()
        val sorted = history.sortedBy { it.date }
        val xs = sorted.indices.map { it.toDouble() }
        val ys = sorted.map { it.price }
        val xMean = xs.average()
        val yMean = ys.average()
        val numerator = xs.indices.sumOf { (xs[it] - xMean) * (ys[it] - yMean) }
        val denominator = xs.indices.sumOf { (xs[it] - xMean).pow(2) }
        if (denominator == 0.0) return emptyList()
        val slope = numerator / denominator
        val intercept = yMean - slope * xMean
        val start = Pair(sorted.first().date, intercept + slope * xs.first())
        val predictedNext = Pair(sorted.last().date + 86_400_000, intercept + slope * (xs.last() + 1))
        return listOf(start, predictedNext)
    }
}
