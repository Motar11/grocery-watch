package com.motar11.grocerywatch.ui.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.motar11.grocerywatch.ui.components.PriceChart
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ItemDetailScreen(
    state: ItemDetailState,
    onSave: (String, String, Int, String, Boolean) -> Unit,
    onAddPrice: (Double, String) -> Unit,
    onToggle: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    val item = state.item?.item
    var name by remember(item) { mutableStateOf(item?.name ?: "") }
    var category by remember(item) { mutableStateOf(item?.category ?: "") }
    var quantity by remember(item) { mutableStateOf(item?.quantity?.toString() ?: "1") }
    var notes by remember(item) { mutableStateOf(item?.notes ?: "") }
    var store by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(item?.name ?: "Item") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = { onToggle(!(item?.completed ?: false)) }) {
                        Icon(Icons.Outlined.CheckCircle, contentDescription = "Complete")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it.filter { ch -> ch.isDigit() } },
                label = { Text("Quantity") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth())
            Button(
                onClick = {
                    onSave(
                        name,
                        category.ifBlank { "Misc" },
                        quantity.toIntOrNull() ?: 1,
                        notes,
                        item?.completed ?: false
                    )
                },
                modifier = Modifier.align(Alignment.End)
            ) { Text("Save changes") }

            Divider()
            Text("Price history", style = MaterialTheme.typography.titleMedium)
            val sortedHistory = state.priceHistory.sortedBy { it.date }
            PriceChart(
                points = sortedHistory.map { it.date to it.price },
                trendLine = state.trendLine,
                modifier = Modifier.fillMaxWidth()
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("Price") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = store,
                    onValueChange = { store = it },
                    label = { Text("Store") },
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = {
                    val priceValue = price.toDoubleOrNull()
                    if (priceValue != null && store.isNotBlank()) {
                        onAddPrice(priceValue, store)
                        price = ""
                    }
                }) { Text("Add") }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.priceHistory) { entry ->
                    PriceRow(store = entry.store, price = entry.price, date = entry.date)
                }
            }
        }
    }
}

@Composable
private fun PriceRow(store: String, price: Double, date: Long) {
    val formatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(store, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
            Text(formatter.format(Date(date)), style = MaterialTheme.typography.bodySmall)
        }
        Text("$${"%.2f".format(price)}", style = MaterialTheme.typography.titleMedium)
    }
}
