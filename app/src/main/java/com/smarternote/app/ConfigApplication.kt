package com.smarternote.app

import androidx.lifecycle.MutableLiveData
import com.smarternote.core.BaseApplication
import dagger.hilt.android.HiltAndroidApp

class ConfigApplication : BaseApplication() {

    val buildType: MutableLiveData<String> = MutableLiveData()

    override fun onCreate() {
        super.onCreate()

        // 初始化和配置应用程序
        buildType.value = getString(R.string.build_type)
    }

    override fun isDebugBuild(): Boolean = buildType.value == "DEBUG"

}