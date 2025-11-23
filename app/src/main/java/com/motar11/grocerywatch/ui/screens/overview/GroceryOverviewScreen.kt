package com.motar11.grocerywatch.ui.screens.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.SaveAlt
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.motar11.grocerywatch.data.local.entity.GroceryItemWithPrices

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryOverviewScreen(
    state: GroceryOverviewState,
    onAddList: (String) -> Unit,
    onDeleteList: (Long) -> Unit,
    onSelectList: (Long) -> Unit,
    onAddItem: (Long, String, String, Int, String) -> Unit,
    onItemComplete: (Long, Boolean) -> Unit,
    onDeleteItem: (Long) -> Unit,
    onNavigateToItem: (Long, Long) -> Unit,
    onNavigateToCompare: () -> Unit,
    onExport: () -> Unit,
    onImport: (String) -> Unit
) {
    var newListName by remember { mutableStateOf("") }
    var itemName by remember { mutableStateOf("") }
    var itemCategory by remember { mutableStateOf(state.categories.firstOrNull() ?: "") }
    var itemQuantity by remember { mutableStateOf("1") }
    var itemNotes by remember { mutableStateOf("") }
    val selectedListId = state.selectedListId

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Grocery Watch") },
                navigationIcon = { Icon(imageVector = Icons.Outlined.List, contentDescription = null) },
                actions = {
                    IconButton(onClick = onNavigateToCompare) {
                        Icon(imageVector = Icons.Outlined.History, contentDescription = "Compare prices")
                    }
                    IconButton(onClick = onExport) {
                        Icon(imageVector = Icons.Outlined.SaveAlt, contentDescription = "Export CSV")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Lists", style = MaterialTheme.typography.titleMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.lists) { list ->
                    val isSelected = list.list.id == selectedListId
                    AssistChip(
                        onClick = { onSelectList(list.list.id) },
                        label = { Text(list.list.name) },
                        leadingIcon = {
                            if (isSelected) {
                                Icon(Icons.Outlined.List, contentDescription = null)
                            }
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            leadingIconContentColor = MaterialTheme.colorScheme.primary,
                            labelColor = MaterialTheme.colorScheme.onSurface,
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                        )
                    )
                    IconButton(onClick = { onDeleteList(list.list.id) }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete list")
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = newListName,
                    onValueChange = { newListName = it },
                    label = { Text("New list name") },
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = {
                    if (newListName.isNotBlank()) {
                        onAddList(newListName)
                        newListName = ""
                    }
                }) { Text("Add") }
            }

            Divider()

            Text("Add item", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = itemCategory,
                onValueChange = { itemCategory = it },
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth()
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.categories) { category ->
                    AssistChip(onClick = { itemCategory = category }, label = { Text(category) })
                }
            }
            OutlinedTextField(
                value = itemQuantity,
                onValueChange = { itemQuantity = it.filter { ch -> ch.isDigit() }.ifBlank { "" } },
                label = { Text("Quantity") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = itemNotes,
                onValueChange = { itemNotes = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    if (selectedListId != null && itemName.isNotBlank()) {
                        onAddItem(
                            selectedListId,
                            itemName,
                            itemCategory.ifBlank { "Misc" },
                            itemQuantity.toIntOrNull() ?: 1,
                            itemNotes
                        )
                        itemName = ""
                        itemNotes = ""
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Outlined.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add to list")
            }

            Divider()
            Text("Items", style = MaterialTheme.typography.titleMedium)
            selectedListId?.let { listId ->
                val itemsForList = state.lists.firstOrNull { it.list.id == listId }
                if (itemsForList == null) {
                    Text("No list selected yet")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(itemsForList.items, key = { it.item.id }) { itemWithPrices ->
                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = {
                                    if (it == SwipeToDismissBoxValue.EndToStart) {
                                        onDeleteItem(itemWithPrices.item.id)
                                    }
                                    true
                                }
                            )
                            SwipeToDismissBox(state = dismissState, backgroundContent = {}) {
                                GroceryItemCard(
                                    itemWithPrices,
                                    onCheckedChange = { checked -> onItemComplete(itemWithPrices.item.id, checked) },
                                    onOpen = { onNavigateToItem(listId, itemWithPrices.item.id) }
                                )
                            }
                        }
                    }
                }
            }

            if (!state.exportedCsv.isNullOrBlank()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("CSV export", style = MaterialTheme.typography.titleMedium)
                        Text(state.exportedCsv!!, style = MaterialTheme.typography.bodySmall)
                        TextButton(onClick = { onImport(state.exportedCsv!!) }) { Text("Import back") }
                    }
                }
            }
        }
    }
}

@Composable
private fun GroceryItemCard(
    item: GroceryItemWithPrices,
    onCheckedChange: (Boolean) -> Unit,
    onOpen: () -> Unit
) {
    val lastPrice = item.priceHistory.maxByOrNull { it.date }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium),
        onClick = onOpen
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = item.item.completed, onCheckedChange = onCheckedChange)
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.item.name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${item.item.category} • Qty ${item.item.quantity}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (lastPrice != null) {
                    Text("$${"%.2f".format(lastPrice.price)}", style = MaterialTheme.typography.titleMedium)
                }
            }
            if (item.item.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(item.item.notes, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
