package com.smarternote.themes

import androidx.appcompat.app.AppCompatActivity

object ThemeManager {
    private val observers = mutableListOf<AppCompatActivity>()
    var currentTheme: Int = R.style.Theme_SmartNote
        set(value) {
            field = value
            notifyObservers()
        }

    fun addObserver(activity: AppCompatActivity) {
        if (!observers.contains(activity)) {
            observers.add(activity)
            activity.setTheme(currentTheme)
        }
    }

    fun removeObserver(activity: AppCompatActivity) {
        observers.remove(activity)
    }

    private fun notifyObservers() {
        observers.forEach { activity ->
            activity.setTheme(currentTheme)
            activity.recreate()
        }
    }
}
