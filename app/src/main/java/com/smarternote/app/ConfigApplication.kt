package com.smarternote.app

import androidx.lifecycle.MutableLiveData
import com.smarternote.core.BaseApplication

class ConfigApplication : BaseApplication() {

    private val buildType: MutableLiveData<String> = MutableLiveData()

    override fun onCreate() {
        super.onCreate()

        // 初始化和配置应用程序
        buildType.value = getString(R.string.build_type)

        initThemes()
    }

    private fun initThemes() {

    }

    override fun isDebugBuild(): Boolean = buildType.value == "DEBUG"

}