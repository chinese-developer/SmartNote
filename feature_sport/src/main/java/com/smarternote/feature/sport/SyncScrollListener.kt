package com.smarternote.feature.sport

import androidx.recyclerview.widget.RecyclerView

class SyncScrollListener(private val onScroll: (dx: Int) -> Unit) : RecyclerView.OnScrollListener() {
    private var isSyncScrolling = false

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (isSyncScrolling) {
            return
        }

        isSyncScrolling = true
        onScroll(dx)
        isSyncScrolling = false
    }
}