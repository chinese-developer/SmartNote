package com.smarternote.core.ui.viewpager.transformer

import android.view.View

import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

/**
 * 淡出变换效果
 */
class FadeOutTransformation : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        page.translationX = -position * page.width
        page.alpha = 1 - abs(position)
    }
}