package com.example.grocerywatch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerywatch.data.entity.GroceryItem
import com.example.grocerywatch.data.entity.GroceryList
import com.example.grocerywatch.data.entity.PriceHistoryEntry
import com.example.grocerywatch.repository.GroceryRepository
import com.example.grocerywatch.repository.PriceComparison
import com.example.grocerywatch.util.TrendCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class GroceryViewModel @Inject constructor(
    private val repository: GroceryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroceryUiState())
    val uiState: StateFlow<GroceryUiState> = _uiState

    private var itemsJob: Job? = null
    private var historyJob: Job? = null

    init {
        observeLists()
        observeComparisons()
    }

    private fun observeLists() {
        viewModelScope.launch {
            repository.groceryLists.collectLatest { lists ->
                val selectedId = _uiState.value.selectedListId ?: lists.firstOrNull()?.id
                _uiState.update { it.copy(lists = lists, selectedListId = selectedId) }
                selectedId?.let { observeItems(it) }
            }
        }
    }

    private fun observeItems(listId: Long) {
        itemsJob?.cancel()
        itemsJob = viewModelScope.launch {
            repository.getItemsForList(listId).collectLatest { items ->
                _uiState.update { it.copy(items = items) }
                setHistoryTargetInternal(items.firstOrNull()?.id)
            }
        }
    }

    fun selectList(id: Long) {
        _uiState.update { it.copy(selectedListId = id) }
        observeItems(id)
    }

    private fun setHistoryTargetInternal(itemId: Long?) {
        historyJob?.cancel()
        if (itemId == null) return
        historyJob = viewModelScope.launch {
            repository.getPriceHistoryForItem(itemId).collectLatest { history ->
                _uiState.update { state ->
                    state.copy(
                        selectedItemHistory = history,
                        trendLine = TrendCalculator.calculate(history)
                    )
                }
            }
        }
    }

    fun setHistoryTarget(itemId: Long) {
        setHistoryTargetInternal(itemId)
    }

    fun setStoreFilter(store: String) {
        _uiState.update { it.copy(storeFilter = store) }
    }

    private fun observeComparisons() {
        viewModelScope.launch {
            uiState.map { it.storeFilter }
                .distinctUntilChanged()
                .collectLatest { filter ->
                    repository.getLowestPrices(filter).collectLatest { comparisons ->
                        _uiState.update { it.copy(comparisons = comparisons) }
                    }
                }
        }
    }

    fun addList(name: String) {
        viewModelScope.launch { repository.addList(name) }
    }

    fun updateList(list: GroceryList) {
        viewModelScope.launch { repository.updateList(list) }
    }

    fun deleteList(list: GroceryList) {
        viewModelScope.launch { repository.deleteList(list) }
    }

    fun addItem(listId: Long, name: String, category: String, quantity: Int, notes: String) {
        viewModelScope.launch { repository.addItem(listId, name, category, quantity, notes) }
    }

    fun updateItem(item: GroceryItem) {
        viewModelScope.launch { repository.updateItem(item) }
    }

    fun toggleComplete(itemId: Long, completed: Boolean) {
        viewModelScope.launch { repository.toggleComplete(itemId, completed) }
    }

    fun deleteItem(item: GroceryItem) {
        viewModelScope.launch { repository.deleteItem(item) }
    }

    fun addPriceEntry(itemId: Long, price: Double, store: String, date: Long) {
        viewModelScope.launch { repository.addPriceEntry(itemId, price, store, date) }
    }

    fun refreshExport() {
        viewModelScope.launch {
            val csv = repository.exportPriceHistoryCsv()
            _uiState.update { it.copy(exportedCsv = csv) }
        }
    }

    fun dismissExportPreview() {
        _uiState.update { it.copy(exportedCsv = null) }
    }
}

data class GroceryUiState(
    val lists: List<GroceryList> = emptyList(),
    val selectedListId: Long? = null,
    val items: List<GroceryItem> = emptyList(),
    val selectedItemHistory: List<PriceHistoryEntry> = emptyList(),
    val comparisons: List<PriceComparison> = emptyList(),
    val storeFilter: String = "",
    val trendLine: TrendCalculator.TrendLine? = null,
    val exportedCsv: String? = null
)
