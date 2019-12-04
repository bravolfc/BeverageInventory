package com.ishwal.beverageinventory.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class InventoryUser (
    val userId : String,
    val fullName : String,
    val email : String,
    val dateOfBirth : String
){
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to userId,
            "fullName" to fullName,
            "email" to email,
            "dateOfBirth" to dateOfBirth
        )
    }
}