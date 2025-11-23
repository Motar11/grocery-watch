package com.example.grocerywatch.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.grocerywatch.data.entity.GroceryItem
import com.example.grocerywatch.data.entity.GroceryList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryListDetailScreen(
    list: GroceryList?,
    items: List<GroceryItem>,
    onToggleComplete: (GroceryItem) -> Unit,
    onDeleteItem: (GroceryItem) -> Unit,
    onAddItem: (String, String, Int, String) -> Unit,
    onOpenHistory: (GroceryItem) -> Unit
) {
    val name = remember { mutableStateOf("") }
    val category = remember { mutableStateOf("General") }
    val quantity = remember { mutableStateOf("1") }
    val notes = remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = list?.name ?: "List", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Add Item", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = name.value,
            onValueChange = { name.value = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = category.value,
            onValueChange = { category.value = it },
            label = { Text("Category") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = quantity.value,
            onValueChange = { quantity.value = it },
            label = { Text("Quantity") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = notes.value,
            onValueChange = { notes.value = it },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                val qty = quantity.value.toIntOrNull() ?: 1
                onAddItem(name.value, category.value, qty, notes.value)
                name.value = ""
                quantity.value = "1"
                notes.value = ""
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Add to list")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Items", style = MaterialTheme.typography.titleMedium)

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items, key = { it.id }) { item ->
                val dismissState = rememberDismissState(confirmValueChange = {
                    onDeleteItem(item)
                    true
                })
                SwipeToDismiss(
                    state = dismissState,
                    background = {},
                    dismissContent = {
                        GroceryItemRow(
                            item = item,
                            onToggleComplete = { onToggleComplete(item.copy(completed = !item.completed)) },
                            onOpenHistory = { onOpenHistory(item) }
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun GroceryItemRow(
    item: GroceryItem,
    onToggleComplete: () -> Unit,
    onOpenHistory: () -> Unit
) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = item.completed, onCheckedChange = { onToggleComplete() })
                    Column {
                        Text(text = item.name, fontWeight = FontWeight.Bold)
                        Text(text = "${item.quantity} • ${item.category}")
                        if (item.notes.isNotBlank()) {
                            Text(text = item.notes, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            IconButton(onClick = onOpenHistory) {
                Icon(Icons.Default.History, contentDescription = "Price history")
            }
        }
    }
}
