package com.ishwal.beverageinventory.ui.inventory.list

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableField
import com.ishwal.beverageinventory.R
import com.ishwal.beverageinventory.model.InventoryItem


class InventoryItemListViewModel (
        context: Context,
        item: InventoryItem
){

    val productName = ObservableField<String>(
        item.productName
    )

    val icon = ObservableField<Drawable>(
        when(item.productType){
            "Wine" -> ContextCompat.getDrawable(context,R.drawable.ic_wine)
            "Others" -> ContextCompat.getDrawable(context,R.drawable.ic_other)
            "Beer" -> ContextCompat.getDrawable(context,R.drawable.ic_beer)
            "Liquor" -> ContextCompat.getDrawable(context,R.drawable.ic_liquor)
            else -> ContextCompat.getDrawable(context,R.drawable.ic_liquor)
        }
    )

    val quantity = ObservableField<String>(
        item.productQuantity.toString()
    )

}