package com.smarternote.core.utils

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.smarternote.core.ui.layoutmanager.SlowLinearLayoutManager

fun ViewPager2.slowScroll() {
    val recyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
    recyclerViewField.isAccessible = true
    val recyclerView = recyclerViewField.get(this) as RecyclerView
    recyclerView.layoutManager = SlowLinearLayoutManager(context)
}