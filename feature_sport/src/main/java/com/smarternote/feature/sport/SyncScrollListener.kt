package com.smarternote.feature.sport

import androidx.recyclerview.widget.RecyclerView

interface SyncScrollListener {
    fun onScrolled(dx: Int)
}


class SyncScrollListenerImpl(
    private val syncScrollListener: SyncScrollListener,
    private val onScroll: (dx: Int) -> Unit
) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        onScroll(dx)
        syncScrollListener.onScrolled(dx)
    }
}