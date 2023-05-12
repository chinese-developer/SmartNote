package com.smarternote.core.ui.recyclerview.layoutmanager

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * 预加载数据
 *
 * 当纵向的列表是 RecyclerView，其每一个 Item 包含 ViewPager2，
 * 该 ViewPaper2 内部维护的 RecyclerView，仅仅展示第一条数据，
 * 即使设置了 offscreenPageLimit 也没办法预加载。
 * 这个类用于处理预加载数据问题
 */
class PreloadLinearLayoutManager(context: Context) : LinearLayoutManager(context, HORIZONTAL, false) {

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        detachAndScrapAttachedViews(recycler)

        val start = paddingLeft
        var currentPosition = start
        val parentBottom = height - paddingBottom
        val parentTop = paddingTop
        for (i in 0 until itemCount) {
            val view = recycler?.getViewForPosition(i)
            if (view != null) {
                addView(view)
                measureChildWithMargins(view, 0, 0)
                val width = getDecoratedMeasuredWidth(view)
                val height = getDecoratedMeasuredHeight(view)
                layoutDecorated(view, currentPosition, parentTop, currentPosition + width, parentBottom)
                currentPosition += width
            }
        }
    }
}
