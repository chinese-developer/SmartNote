package com.smarternote.feature.sport

import androidx.recyclerview.widget.RecyclerView

interface OnScrollListener {
    fun onScroll(dx: Int)
}

class HorizontalScrollListener : RecyclerView.OnScrollListener() {

    val observers = mutableListOf<OnScrollListener>()

    fun addObserver(observer: OnScrollListener) {
        if (!observers.contains(observer)) {
            observers.add(observer)
        }
    }

    fun removeObserver(observer: HorizontalScrollObserver) {
        if (observers.contains(observer)) {
            observers.remove(observer)
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        observers.forEach { observer ->
            observer.onScroll(recyclerView, dx)
        }
    }
}