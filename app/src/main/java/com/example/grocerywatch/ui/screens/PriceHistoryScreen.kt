package com.example.grocerywatch.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.grocerywatch.data.entity.PriceHistoryEntry
import com.example.grocerywatch.ui.components.PriceChart
import com.example.grocerywatch.util.TrendCalculator
import java.text.DateFormat

@Composable
fun PriceHistoryScreen(
    itemName: String?,
    history: List<PriceHistoryEntry>,
    trendLine: TrendCalculator.TrendLine?,
    onAddEntry: (Double, String) -> Unit
) {
    val price = remember { mutableStateOf("") }
    val store = remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Price history for ${itemName ?: "Item"}", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(12.dp))
        PriceChart(history = history, trend = trendLine)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = trendLine?.let { "Predicted next price: ${"%.2f".format(it.predictedNextPrice)}" } ?: "Add more data for a trend")

        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = price.value,
            onValueChange = { price.value = it },
            label = { Text("Price") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = store.value,
            onValueChange = { store.value = it },
            label = { Text("Store") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = {
            val parsed = price.value.toDoubleOrNull() ?: return@Button
            onAddEntry(parsed, store.value)
            price.value = ""
            store.value = ""
        }, modifier = Modifier.padding(top = 8.dp)) {
            Text("Add price")
        }

        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn {
            items(history) { entry ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "$${entry.price}")
                        Text(text = entry.store)
                        Text(text = DateFormat.getDateInstance().format(entry.date))
                    }
                }
            }
        }
    }
}
