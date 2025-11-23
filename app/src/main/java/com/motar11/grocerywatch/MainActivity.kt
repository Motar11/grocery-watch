package com.motar11.grocerywatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.motar11.grocerywatch.ui.navigation.GroceryNavHost
import com.motar11.grocerywatch.ui.theme.GroceryWatchTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            GroceryWatchTheme {
                GroceryNavHost()
            }
        }
    }
}
