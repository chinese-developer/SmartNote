package com.smarternote.themes

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData

object ThemeManager {
    private val themeObservers = mutableListOf<AppCompatActivity>()

    /**
     * 为所有的 DynamicView 动态的提供配色方案
     */
    val dynamicViewsThemeConfig = MutableLiveData<ThemeConfig>().apply {
        value = ThemeConfig(
            textColor = R.color.text_color_primary
            // 设置其他属性的默认值
        )
    }

    val statusBarColor = MutableLiveData<Int>().apply { value = R.color.statusBarColor }

    var currentTheme: Int = R.style.Theme_SmartNote
        set(value) {
            field = value
            notifyThemeObservers()
        }

    fun addThemeObserver(activity: AppCompatActivity) {
        if (!themeObservers.contains(activity)) {
            themeObservers.add(activity)
            activity.setTheme(currentTheme)
        }
    }

    fun removeThemeObserver(activity: AppCompatActivity) {
        themeObservers.remove(activity)
    }

    private fun notifyThemeObservers() {
        themeObservers.forEach { activity ->
            activity.setTheme(currentTheme)
            activity.recreate()
        }
    }

}
