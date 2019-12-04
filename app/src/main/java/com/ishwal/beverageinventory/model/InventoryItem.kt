package com.ishwal.beverageinventory.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize
import org.parceler.Parcel

@Parcelize
@IgnoreExtraProperties
class InventoryItem(
    @Exclude
    var itemId: String?,
    val userId: String,
    val productName: String,
    val productType: String,
    val productBarCode: String?,
    val productVolume: String?,
    val productVolumeUnit: String?,
    val productQuantity: Int?
) : Parcelable {

    constructor() : this(null, "", "Not Found", "", "", "", "", 0)

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to userId,
            "productName" to productName,
            "productType" to productType,
            "productBarCode" to productBarCode,
            "productVolume" to productVolume,
            "productVolumeUnit" to productVolumeUnit,
            "productQuantity" to productQuantity
        )
    }
}