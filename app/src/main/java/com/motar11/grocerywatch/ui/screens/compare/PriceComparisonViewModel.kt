package com.motar11.grocerywatch.ui.screens.compare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motar11.grocerywatch.data.repository.GroceryRepository
import com.motar11.grocerywatch.data.repository.PriceSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn


data class PriceComparisonState(
    val storeFilter: String? = null,
    val prices: List<PriceSnapshot> = emptyList()
)

@HiltViewModel
class PriceComparisonViewModel @Inject constructor(
    private val repository: GroceryRepository
) : ViewModel() {

    private val storeFilter = MutableStateFlow<String?>(null)

    val state = storeFilter
        .flatMapLatest { repository.observeLowestPrices(it) }
        .combine(storeFilter) { prices, filter ->
            PriceComparisonState(filter, prices)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PriceComparisonState())

    fun updateFilter(filter: String?) {
        storeFilter.value = filter?.takeIf { it.isNotBlank() }
    }
}
