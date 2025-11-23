package com.motar11.grocerywatch.ui.screens.compare

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.motar11.grocerywatch.data.repository.PriceSnapshot
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ComparePricesScreen(
    state: PriceComparisonState,
    onFilterChange: (String?) -> Unit,
    onBack: () -> Unit
) {
    var filter by remember(state.storeFilter) { mutableStateOf(state.storeFilter ?: "") }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compare prices") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = filter,
                onValueChange = {
                    filter = it
                    onFilterChange(it.ifBlank { null })
                },
                label = { Text("Filter by store") },
                modifier = Modifier.fillMaxWidth()
            )
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.prices) { snapshot ->
                    PriceComparisonCard(snapshot)
                }
            }
        }
    }
}

@Composable
private fun PriceComparisonCard(snapshot: PriceSnapshot) {
    val formatter = remember { SimpleDateFormat("MMM dd", Locale.getDefault()) }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(snapshot.itemName, style = MaterialTheme.typography.titleMedium)
                    Text("List: ${snapshot.listName}", style = MaterialTheme.typography.bodySmall)
                }
                Text("$${"%.2f".format(snapshot.price)}", style = MaterialTheme.typography.headlineSmall)
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Store: ${snapshot.store}")
                Text("Updated ${formatter.format(Date(snapshot.lastUpdated))}")
            }
        }
    }
}
