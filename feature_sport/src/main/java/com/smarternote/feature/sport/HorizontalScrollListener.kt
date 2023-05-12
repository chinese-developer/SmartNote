package com.smarternote.feature.sport

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

interface HorizontalScrollObserver {
    fun scrollBy(dx: Int)
    fun onScroll(position: Int, offset: Int)
    fun getHolder(): RecyclerView.ViewHolder
}

class HorizontalScrollListener(private val mainObserver: HorizontalScrollObserver) : RecyclerView.OnScrollListener() {
    private var lastPosition = -1
    private var lastOffset = -1

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        (recyclerView.layoutManager as? LinearLayoutManager)?.let { layoutManager ->
            val position = layoutManager.findFirstVisibleItemPosition()
            val offset = layoutManager.findViewByPosition(position)?.left ?: 0
            if (lastPosition != position || lastOffset != offset) {
                lastPosition = position
                lastOffset = offset
                mainObserver.onScroll(position, offset)
            }
        }

        val rvParent = (mainObserver.getHolder() as? ListActivity.VerticalRecyclerViewAdapter.VH)?.binding?.root?.parent as? RecyclerView
        rvParent?.let { parent ->
            for (i in 0 until parent.childCount) {
                val child = parent.getChildAt(i)
                val viewHolder = parent.getChildViewHolder(child)
                if (viewHolder is ListActivity.VerticalRecyclerViewAdapter.VH && viewHolder != mainObserver.getHolder()) {
//                    viewHolder.smoothScrollBy(dx)
                }
            }
        }
    }

//    private fun RecyclerView.ViewHolder.smoothScrollBy(dx: Int) {
//        (this as? ListActivity.ListAdapter.VH)?.binding?.horizontalRecyclerView?.let { recyclerView ->
//            if (recyclerView.isLaidOut) {
//                recyclerView.smoothScrollBy(dx, 0)
//            }
//        }
//    }
}

