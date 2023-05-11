package com.smarternote.feature.sport

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SyncScrollLinearLayoutManager(
    context: Context,
    private val onScrollListener: (dx: Int) -> Unit
) : LinearLayoutManager(context, HORIZONTAL, false) {

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        onScrollListener(dx)
        return super.scrollHorizontallyBy(dx, recycler, state)
    }
}