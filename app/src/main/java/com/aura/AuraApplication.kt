package com.aura

import android.app.Application
import com.aura.data.di.AppModule
import com.aura.data.di.AppModuleImpl

class AuraApplication : Application() {

    companion object {
        lateinit var appModule: AppModule
    }

    override fun onCreate() {
        super.onCreate()
        appModule = AppModuleImpl()
    }
}