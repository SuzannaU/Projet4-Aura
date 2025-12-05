package com.aura

import android.app.Application
import com.aura.di.AppModule
import com.aura.di.AppModuleImpl

class AuraApplication : Application() {

    companion object {
        lateinit var appModule: AppModule
    }

    override fun onCreate() {
        super.onCreate()
        appModule = AppModuleImpl(AppConstants.BASE_URL)
    }
}