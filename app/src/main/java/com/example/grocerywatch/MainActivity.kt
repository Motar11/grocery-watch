package com.example.grocerywatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.grocerywatch.ui.navigation.Screen
import com.example.grocerywatch.ui.screens.ComparePricesScreen
import com.example.grocerywatch.ui.screens.GroceryListDetailScreen
import com.example.grocerywatch.ui.screens.GroceryListsScreen
import com.example.grocerywatch.ui.screens.PriceHistoryScreen
import com.example.grocerywatch.ui.theme.GroceryWatchTheme
import com.example.grocerywatch.viewmodel.GroceryViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GroceryWatchTheme {
                val navController = rememberNavController()
                val viewModel: GroceryViewModel = hiltViewModel()
                val state by viewModel.uiState.collectAsState()

                Scaffold(
                    topBar = {
                        SmallTopAppBar(
                            title = { Text("Grocery Watch") },
                            actions = {
                                IconButton(onClick = { navController.navigate(Screen.Compare.route) }) {
                                    Icon(Icons.Default.Compare, contentDescription = "Compare")
                                }
                            }
                        )
                    }
                ) { padding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Lists.route,
                        modifier = Modifier.padding(padding)
                    ) {
                        composable(Screen.Lists.route) {
                            GroceryListsScreen(
                                lists = state.lists,
                                exportedCsv = state.exportedCsv,
                                onAddList = { viewModel.addList(it) },
                                onOpenList = { list -> navController.navigate(Screen.Detail.create(list.id)) },
                                onOpenCompare = { navController.navigate(Screen.Compare.route) },
                                onExport = { viewModel.refreshExport() },
                                onDismissExport = { viewModel.dismissExportPreview() }
                            )
                        }

                        composable(
                            route = Screen.Detail.route,
                            arguments = listOf(navArgument("listId") { type = NavType.LongType })
                        ) { entry ->
                            val listId = entry.arguments?.getLong("listId") ?: return@composable
                            LaunchedEffect(listId) { viewModel.selectList(listId) }
                            val list = state.lists.firstOrNull { it.id == listId }
                            GroceryListDetailScreen(
                                list = list,
                                items = state.items,
                                onToggleComplete = { viewModel.toggleComplete(it.id, it.completed) },
                                onDeleteItem = { viewModel.deleteItem(it) },
                                onAddItem = { name, category, quantity, notes ->
                                    viewModel.addItem(listId, name, category, quantity, notes)
                                },
                                onOpenHistory = { item ->
                                    navController.navigate(Screen.PriceHistory.create(item.id))
                                    viewModel.setHistoryTarget(item.id)
                                }
                            )
                        }

                        composable(
                            route = Screen.PriceHistory.route,
                            arguments = listOf(navArgument("itemId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val itemId = backStackEntry.arguments?.getLong("itemId") ?: return@composable
                            LaunchedEffect(itemId) { viewModel.setHistoryTarget(itemId) }
                            val item = state.items.firstOrNull { it.id == itemId }
                            PriceHistoryScreen(
                                itemName = item?.name,
                                history = state.selectedItemHistory,
                                trendLine = state.trendLine,
                                onAddEntry = { price, store ->
                                    viewModel.addPriceEntry(itemId, price, store, System.currentTimeMillis())
                                }
                            )
                        }

                        composable(Screen.Compare.route) {
                            ComparePricesScreen(
                                comparisons = state.comparisons,
                                currentFilter = state.storeFilter,
                                onFilterChanged = { viewModel.setStoreFilter(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}
