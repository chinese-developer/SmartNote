package com.smarternote.core.ui.base

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import kotlin.properties.Delegates

abstract class BaseActivity : AppCompatActivity() {

    private var themeOverlay by Delegates.notNull<Int>()

    override fun attachBaseContext(newBase: Context) {
        val contextThemeWrapper = ContextThemeWrapper(newBase, themeOverlay)
        super.attachBaseContext(contextThemeWrapper)
    }

    fun setThemeOverlay(themeOverlay: Int) {
        this.themeOverlay = themeOverlay
    }
}