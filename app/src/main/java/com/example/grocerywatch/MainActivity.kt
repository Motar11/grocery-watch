package com.example.grocerywatch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grocerywatch.databinding.ActivityMainBinding

/**
 * Main screen that lets the user add grocery items and remove them by tapping.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Holds the current list of items shown by the adapter.
    private val groceryItems = mutableListOf<GroceryItem>()
    private lateinit var adapter: GroceryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupAddButton()
    }

    /**
     * Configures the RecyclerView with a simple vertical list.
     */
    private fun setupRecyclerView() {
        adapter = GroceryAdapter(groceryItems) { item ->
            // Remove the tapped item from the list.
            adapter.removeItem(item)
        }

        binding.recyclerViewItems.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewItems.adapter = adapter
    }

    /**
     * Handles clicks on the Add button to insert new items.
     */
    private fun setupAddButton() {
        binding.buttonAdd.setOnClickListener {
            val name = binding.editTextItem.text?.toString()?.trim().orEmpty()

            if (name.isNotEmpty()) {
                adapter.addItem(GroceryItem(name))
                binding.editTextItem.text?.clear()
                binding.recyclerViewItems.scrollToPosition(groceryItems.size - 1)
            }
        }
    }
}
