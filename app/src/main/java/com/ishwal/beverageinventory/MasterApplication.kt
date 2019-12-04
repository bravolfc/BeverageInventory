package com.ishwal.beverageinventory

import android.app.Application
import com.ishwal.beverageinventory.di.inventoryAppModule
import net.danlew.android.joda.JodaTimeAndroid
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber


class MasterApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        JodaTimeAndroid.init(this)

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@MasterApplication)
            modules(inventoryAppModule)
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

    }


}