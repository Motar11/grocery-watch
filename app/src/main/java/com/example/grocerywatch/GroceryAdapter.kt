package com.example.grocerywatch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerywatch.databinding.ItemGroceryBinding

/**
 * RecyclerView adapter that shows simple text-only grocery items.
 */
class GroceryAdapter(
    private val items: MutableList<GroceryItem>,
    private val onItemClick: (GroceryItem) -> Unit
) : RecyclerView.Adapter<GroceryAdapter.GroceryViewHolder>() {

    /**
     * ViewHolder that binds a single grocery item to the layout.
     */
    class GroceryViewHolder(
        private val binding: ItemGroceryBinding,
        private val onItemClick: (GroceryItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GroceryItem) {
            binding.textItemName.text = item.name
            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemGroceryBinding.inflate(inflater, parent, false)
        return GroceryViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: GroceryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    /**
     * Adds a new grocery item to the list and updates the UI.
     */
    fun addItem(item: GroceryItem) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    /**
     * Removes the provided item if it exists in the list.
     */
    fun removeItem(item: GroceryItem) {
        val index = items.indexOf(item)
        if (index != -1) {
            items.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}
