package com.smarternote.themes.extensions

import androidx.appcompat.app.AppCompatActivity
import com.smarternote.themes.R
import com.smarternote.themes.ThemeManager

fun AppCompatActivity.registerThemeObserver() {
    ThemeManager.addThemeObserver(this)
}

fun AppCompatActivity.unregisterThemeObserver() {
    ThemeManager.removeThemeObserver(this)
}

fun AppCompatActivity.switchTheme(themeResId: Int) {
    ThemeManager.currentTheme = themeResId
}

fun AppCompatActivity.applyFeatureSportTheme() {
    switchTheme(R.style.Theme_SmartNote)
}
