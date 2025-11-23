package com.motar11.grocerywatch.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.motar11.grocerywatch.ui.screens.compare.ComparePricesScreen
import com.motar11.grocerywatch.ui.screens.compare.PriceComparisonViewModel
import com.motar11.grocerywatch.ui.screens.detail.ItemDetailScreen
import com.motar11.grocerywatch.ui.screens.detail.ItemDetailViewModel
import com.motar11.grocerywatch.ui.screens.overview.GroceryOverviewScreen
import com.motar11.grocerywatch.ui.screens.overview.GroceryOverviewViewModel

sealed class Destination(val route: String) {
    object Overview : Destination("overview")
    object Compare : Destination("compare")
    object ItemDetail : Destination("item/{itemId}") {
        fun route(itemId: Long) = "item/$itemId"
    }
}

@Composable
fun GroceryNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Destination.Overview.route) {
        composable(Destination.Overview.route) {
            val viewModel: GroceryOverviewViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            GroceryOverviewScreen(
                state = state,
                onAddList = viewModel::addList,
                onDeleteList = viewModel::deleteList,
                onSelectList = viewModel::selectList,
                onAddItem = viewModel::addItem,
                onItemComplete = viewModel::toggleComplete,
                onDeleteItem = viewModel::deleteItem,
                onNavigateToItem = { _, itemId -> navController.navigate(Destination.ItemDetail.route(itemId)) },
                onNavigateToCompare = { navController.navigate(Destination.Compare.route) },
                onExport = viewModel::exportData,
                onImport = viewModel::importData
            )
        }
        composable(Destination.Compare.route) {
            val viewModel: PriceComparisonViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            ComparePricesScreen(
                state = state,
                onFilterChange = viewModel::updateFilter,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Destination.ItemDetail.route,
            arguments = listOf(navArgument("itemId") { type = NavType.LongType })
        ) {
            val viewModel: ItemDetailViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            ItemDetailScreen(
                state = state,
                onSave = viewModel::updateItem,
                onAddPrice = { price, store -> viewModel.addPrice(price, store, System.currentTimeMillis()) },
                onToggle = viewModel::toggleComplete,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
