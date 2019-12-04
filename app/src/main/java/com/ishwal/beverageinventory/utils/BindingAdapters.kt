package com.ishwal.beverageinventory.utils

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("inventoryErrorText")
fun TextInputLayout.setInventoryErrorText(errorMessage: String) {
    if (errorMessage.isBlank()) {
        this.isErrorEnabled = false
    } else {
        this.isErrorEnabled = true
        this.error = errorMessage
    }
}
