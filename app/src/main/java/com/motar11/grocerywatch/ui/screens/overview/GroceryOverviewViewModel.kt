package com.motar11.grocerywatch.ui.screens.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motar11.grocerywatch.data.local.entity.GroceryItemEntity
import com.motar11.grocerywatch.data.local.entity.GroceryListWithItems
import com.motar11.grocerywatch.data.repository.GroceryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class GroceryOverviewState(
    val lists: List<GroceryListWithItems> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedListId: Long? = null,
    val exportedCsv: String? = null
)

@HiltViewModel
class GroceryOverviewViewModel @Inject constructor(
    private val repository: GroceryRepository
) : ViewModel() {

    private val selectedList = MutableStateFlow<Long?>(null)
    private val exportedCsv = MutableStateFlow<String?>(null)

    val state = combine(
        repository.observeLists(),
        repository.observeCategories(),
        selectedList,
        exportedCsv
    ) { lists, categories, selected, export ->
        val firstListId = selected ?: lists.firstOrNull()?.list?.id
        GroceryOverviewState(
            lists = lists,
            categories = categories.map { it.name },
            selectedListId = firstListId,
            exportedCsv = export
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GroceryOverviewState())

    fun selectList(listId: Long) {
        selectedList.value = listId
    }

    fun addList(name: String) {
        viewModelScope.launch {
            repository.addList(name)
        }
    }

    fun deleteList(listId: Long) {
        viewModelScope.launch { repository.deleteList(listId) }
    }

    fun addItem(
        listId: Long,
        name: String,
        category: String,
        quantity: Int,
        notes: String
    ) {
        viewModelScope.launch {
            repository.addOrUpdateItem(
                GroceryItemEntity(
                    listId = listId,
                    name = name,
                    category = category,
                    quantity = quantity,
                    notes = notes
                )
            )
        }
    }

    fun toggleComplete(itemId: Long, completed: Boolean) {
        viewModelScope.launch { repository.toggleCompleted(itemId, completed) }
    }

    fun deleteItem(itemId: Long) {
        viewModelScope.launch { repository.deleteItem(itemId) }
    }

    fun exportData() {
        viewModelScope.launch { exportedCsv.value = repository.exportCsv() }
    }

    fun importData(csv: String) {
        viewModelScope.launch { repository.importCsv(csv) }
    }
}
