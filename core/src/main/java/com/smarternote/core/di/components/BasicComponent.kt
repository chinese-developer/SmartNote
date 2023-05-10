package com.smarternote.core.di.components

import com.alibaba.android.arouter.launcher.ARouter
import com.smarternote.core.BaseApplication
import com.smarternote.core.di.BaseAppComponent

class BasicComponent(private val context: BaseApplication) : BaseAppComponent() {

    override fun init() {
        ARouter.init(context)
    }
}