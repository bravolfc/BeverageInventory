package com.ishwal.beverageinventory.utils.scanner

import java.io.Serializable

data class ScanResultModel(
        val barcodeData: String,
        val requestCode: Int
) : Serializable