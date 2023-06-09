package com.smarternote.core.ui.viewpager.transformer

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

/**
 * 快速旋转变换效果
 */
class FidgetSpinTransformation : ViewPager2.PageTransformer {
    
    override fun transformPage(page: View, position: Float) {

        page.translationX = -position * page.width

        if (abs(position) < 0.5) {
            page.visibility = View.VISIBLE
            page.scaleX = 1 - abs(position)
            page.scaleY = 1 - abs(position)
        } else if (abs(position) > 0.5) {
            page.visibility = View.GONE
        }

        when {
            position < -1 -> // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.alpha = 0f

            position <= 0 -> {    // [-1,0]
                page.alpha = 1f
                page.rotation = 36000 * (abs(position) * abs(position) * abs(position) * abs(
                    position
                ) * abs(position) * abs(position) * abs(position))

            }

            position <= 1 -> {    // (0,1]
                page.alpha = 1f
                page.rotation = -36000 * (abs(position) * abs(position) * abs(position) * abs(
                    position
                ) * abs(position) * abs(position) * abs(position))

            }

            else -> // (1,+Infinity]
                // This page is way off-screen to the right.
                page.alpha = 0f
        }
    }
}