package com.smarternote.feature.sport

import androidx.recyclerview.widget.RecyclerView

interface HorizontalScrollObserver {
    fun onScroll(source: RecyclerView, dx: Int)
}

class HorizontalScrollListener : RecyclerView.OnScrollListener() {

    val observers = mutableListOf<HorizontalScrollObserver>()

    fun addObserver(observer: HorizontalScrollObserver) {
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