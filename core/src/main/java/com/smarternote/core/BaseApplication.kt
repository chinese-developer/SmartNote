package com.smarternote.core

import android.app.Application
import com.smarternote.core.di.BaseAppComponent
import com.smarternote.core.di.ComponentManager


abstract class BaseApplication : Application() {
    companion object {
        lateinit var instance: BaseApplication
            private set
    }

    private val componentManager: ComponentManager by lazy { ComponentManager() }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // 初始化组件
        componentManager.initComponents(this)
    }

    fun <T : BaseAppComponent> getComponent(componentClass: Class<T>): T? {
        return componentManager.getComponent(componentClass)
    }
}