package com.ishwal.beverageinventory.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.koin.core.qualifier.named
import org.koin.dsl.module

val firebaseModule = module {

    factory {
        FirebaseAuth.getInstance()
    }

    factory(qualifier = named("USER")){
        FirebaseDatabase.getInstance().reference.child("users")
    }

    factory(qualifier = named("ITEM")){
        FirebaseDatabase.getInstance().reference.child("users_items")
    }
}
