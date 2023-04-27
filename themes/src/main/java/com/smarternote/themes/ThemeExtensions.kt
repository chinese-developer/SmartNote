package com.smarternote.themes

import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.registerThemeObserver() {
    ThemeManager.addObserver(this)
}

fun AppCompatActivity.unregisterThemeObserver() {
    ThemeManager.removeObserver(this)
}

fun AppCompatActivity.switchTheme(themeResId: Int) {
    ThemeManager.currentTheme = themeResId
}

fun AppCompatActivity.applyModuleATheme() {
    switchTheme(R.style.Theme_SmartNote)
}

fun AppCompatActivity.applyModuleBTheme() {
    switchTheme(R.style.Theme_SmartNote)
}