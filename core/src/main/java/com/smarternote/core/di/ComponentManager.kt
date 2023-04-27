@file:Suppress("UNCHECKED_CAST")

package com.smarternote.core.di

import android.content.Context
import com.smarternote.core.di.components.DataStoreComponent
import com.smarternote.core.di.components.LoggerComponent
import com.smarternote.core.di.components.NetworkComponent

class ComponentManager {
    private val components = mutableMapOf<Class<out BaseAppComponent>, BaseAppComponent>()

    fun initComponents(context: Context, isDebugBuild: Boolean) {
        // 初始化自定义组件
        registerComponent(LoggerComponent(context))
        registerComponent(NetworkComponent(context, isDebugBuild))
        registerComponent(DataStoreComponent(context))

        components.values.forEach { it.init() }
    }

    fun <T : BaseAppComponent> getComponent(componentClass: Class<T>): T? {
        return components[componentClass] as? T
    }

    private fun registerComponent(component: BaseAppComponent) {
        components[component::class.java] = component
    }
}
