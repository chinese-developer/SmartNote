package com.smarternote.core.ui.viewpager.transformer

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs
import kotlin.math.max


class ZoomOutTransformation : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {

        when {
            position < -1 -> // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.alpha = 0f

            position <= 1 -> { // [-1,1]

                page.scaleX = max(MIN_SCALE, 1 - abs(position))
                page.scaleY = max(MIN_SCALE, 1 - abs(position))
                page.alpha = max(MIN_ALPHA, 1 - abs(position))

            }

            else -> // (1,+Infinity]
                // This page is way off-screen to the right.
                page.alpha = 0f
        }


    }

    companion object {

        private const val MIN_SCALE = 0.65f
        private const val MIN_ALPHA = 0.3f
    }
}