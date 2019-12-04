package com.ishwal.beverageinventory.ui.inventory

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ishwal.beverageinventory.R
import com.ishwal.beverageinventory.databinding.InventoryItemRowBinding
import com.ishwal.beverageinventory.model.InventoryItem
import com.ishwal.beverageinventory.ui.inventory.list.InventoryItemListViewModel
import com.ishwal.beverageinventory.ui.inventory.list.InventoryListDiffCallback
import kotlinx.android.synthetic.main.inventory_item_row.view.*
import timber.log.Timber

class InventoryAdapter(
    private val onInventoryItemClickListener: OnInventoryItemClickListener
) :
    ListAdapter<InventoryItem, InventoryAdapter.ViewHolder>(InventoryListDiffCallback()),
    Filterable {


    private var originalList: MutableList<InventoryItem> = mutableListOf()

    private var filteredItemList: List<InventoryItem> = mutableListOf()


    override fun getFilter(): Filter {
        return object : Filter() {
            @SuppressLint("DefaultLocale")
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val charString = charSequence.toString()
                Timber.d("Filter String $charSequence")
                filteredItemList = if (charString.trim().isBlank()) {
                    originalList
                } else {
                    val filteredList: MutableList<InventoryItem> = mutableListOf()
                    for (item in originalList) {
                        if (item.productName.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(item)
                            Timber.d("Filter Result $filteredList")
                        }
                    }
                    filteredList
                }
                val filterResult = FilterResults()
                filterResult.values = filteredItemList
                return filterResult
            }

            override fun publishResults(charSequence: CharSequence?, filterResult: FilterResults?) {
                if (filterResult?.values is MutableList<*>)
                    filteredItemList = filterResult.values as List<InventoryItem>
                this@InventoryAdapter.submitList(filteredItemList)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, itemType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.inventory_item_row, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position).let { inventoryItem ->
            with(holder) {
                itemView.tag = inventoryItem
                bind(
                    inventoryItem
                )
            }
        }
    }

    fun filterBy(filterType: String) {
        val sortedList : MutableList<InventoryItem> = mutableListOf()
        sortedList.addAll(this.originalList)
        when (filterType) {
            "By Quantity(Asc)" -> {
                sortedList.sortBy {
                    it.productQuantity
                }
                this.submitList(sortedList)
            }
            "By Quantity(Desc)" -> {
                sortedList.sortByDescending {
                    it.productQuantity
                }
                this.submitList(sortedList)
            }
            "Only Beer" -> {
                sortedList.retainAll {
                    it.productType == "Beer"
                }
                this.submitList(sortedList)
            }
            "Only Wine" -> {
                sortedList.retainAll {
                    it.productType == "Wine"
                }
                this.submitList(sortedList)
            }
            "Only Liquor" -> {
                sortedList.retainAll {
                    it.productType == "Liquor"
                }
                this.submitList(sortedList)
            }
            "Other" -> {
                sortedList.retainAll {
                    it.productType == "Others"
                }
                this.submitList(sortedList)
            }
        }
        notifyDataSetChanged()
    }

    fun setOriginalList(inventoryItemList: MutableList<InventoryItem>) {
        originalList = inventoryItemList
    }


    inner class ViewHolder(
        private val binding: InventoryItemRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(inventoryItem: InventoryItem) {
            with(binding) {
                inventoryListViewModel = InventoryItemListViewModel(
                    itemView.context,
                    inventoryItem
                )

                root.inventoryItemCard.setOnClickListener {
                    onInventoryItemClickListener.onInventoryItemClicked(inventoryItem)
                }

                root.inventoryItemCard.setOnLongClickListener {
                    onInventoryItemClickListener.onInventoryItemLongClicked(inventoryItem)
                    return@setOnLongClickListener true
                }
            }

        }

    }


    interface OnInventoryItemClickListener {
        fun onInventoryItemClicked(inventoryItem: InventoryItem)
        fun onInventoryItemLongClicked(inventoryItem: InventoryItem)
    }
}