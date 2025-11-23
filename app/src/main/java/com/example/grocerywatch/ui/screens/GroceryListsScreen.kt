package com.example.grocerywatch.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.grocerywatch.data.entity.GroceryList

@Composable
fun GroceryListsScreen(
    lists: List<GroceryList>,
    exportedCsv: String?,
    onAddList: (String) -> Unit,
    onOpenList: (GroceryList) -> Unit,
    onOpenCompare: () -> Unit,
    onExport: () -> Unit,
    onDismissExport: () -> Unit
) {
    val showDialog = remember { mutableStateOf(false) }
    val name = remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Your Grocery Lists", style = MaterialTheme.typography.headlineSmall)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onExport) {
                    Icon(imageVector = Icons.Default.FileDownload, contentDescription = "Export")
                }
                Button(onClick = onOpenCompare) {
                    Text("Compare")
                }
            }
        }

        Divider()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            items(lists) { list ->
                GroceryListCard(list = list, onClick = { onOpenList(list) })
            }
        }

        FloatingActionButton(
            onClick = { showDialog.value = true },
            modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add list")
        }

        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                confirmButton = {
                    Button(onClick = {
                        onAddList(name.value)
                        name.value = ""
                        showDialog.value = false
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog.value = false }) { Text("Cancel") }
                },
                title = { Text("Create list") },
                text = {
                    OutlinedTextField(
                        value = name.value,
                        onValueChange = { name.value = it },
                        label = { Text("List name") }
                    )
                }
            )
        }

        exportedCsv?.let {
            AlertDialog(
                onDismissRequest = onDismissExport,
                confirmButton = {
                    Button(onClick = onDismissExport) { Text("Close") }
                },
                title = { Text("Exported CSV") },
                text = { Text(it, maxLines = 10, overflow = TextOverflow.Ellipsis) }
            )
        }
    }
}

@Composable
private fun GroceryListCard(list: GroceryList, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = list.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Created: ${java.text.DateFormat.getDateInstance().format(list.createdAt)}")
        }
    }
}
