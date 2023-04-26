package com.smarternote.core.di.components

import android.content.Context
import com.smarternote.core.di.BaseAppComponent

class LoggerComponent(private val context: Context) : BaseAppComponent() {
    override fun init() {
        // 初始化日志库
    }
}