package com.example.grocerywatch.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.grocerywatch.repository.PriceComparison

@Composable
fun ComparePricesScreen(
    comparisons: List<PriceComparison>,
    currentFilter: String,
    onFilterChanged: (String) -> Unit
) {
    val filter = remember(currentFilter) { mutableStateOf(currentFilter) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Compare prices", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(
            value = filter.value,
            onValueChange = {
                filter.value = it
                onFilterChanged(it)
            },
            label = { Text("Filter by store") },
            modifier = Modifier.fillMaxWidth()
        )
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(comparisons) { comparison ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = comparison.itemName, style = MaterialTheme.typography.titleMedium)
                        Text(text = "Lowest: ${comparison.store} - $${comparison.price}")
                    }
                }
            }
        }
    }
}
