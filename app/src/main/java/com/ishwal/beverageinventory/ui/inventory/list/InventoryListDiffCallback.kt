package com.ishwal.beverageinventory.ui.inventory.list


import androidx.recyclerview.widget.DiffUtil
import com.ishwal.beverageinventory.model.InventoryItem


class InventoryListDiffCallback : DiffUtil.ItemCallback<InventoryItem>() {

    override fun areItemsTheSame(oldItem: InventoryItem, newItem: InventoryItem): Boolean {
        return oldItem.itemId == newItem.itemId
    }

    override fun areContentsTheSame(oldItem: InventoryItem, newItem: InventoryItem): Boolean {
        return oldItem == newItem
    }


}