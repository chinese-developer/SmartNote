package com.smarternote.core.ui.viewpager.transformer

import android.view.View
import androidx.viewpager2.widget.ViewPager2


class ParallaxTransformation : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        val width = page.width
        if (position < -1) {
            page.scrollX = (width * 0.75 * -1).toInt()
        } else if (position <= 1) {
            if (position < 0) {
                page.scrollX = (width * 0.75 * position).toInt()
            } else {
                page.scrollX = (width * 0.75 * position).toInt()
            }
        } else {
            page.scrollX = (width * 0.75).toInt()
        }
    }
}