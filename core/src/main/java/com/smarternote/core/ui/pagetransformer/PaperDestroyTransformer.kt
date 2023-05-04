package com.smarternote.core.ui.pagetransformer

import android.view.View
import androidx.viewpager2.widget.ViewPager2

class PaperDestroyTransformer : ViewPager2.PageTransformer {

    private val pageTranslationX: Float = 0.1f
    private val minScale: Float = 0.8f
    private val minAlpha: Float = 0.5f

    override fun transformPage(page: View, position: Float) {
        when {
            position < -1 -> { // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.alpha = 0f
            }
            position <= 0 -> { // [-1,0]
                // Use the default slide transition when moving to the left page
                page.translationX = 0f
                page.scaleX = 1f
                page.scaleY = 1f
                page.alpha = 1f
            }
            position <= 1 -> { // (0,1]
                // Fade the page out.
                page.alpha = minAlpha + (1 - minAlpha) * (1 - position)

                // Counteract the default slide transition
                page.translationX = page.width * -position * pageTranslationX

                // Scale the page down (between minScale and 1)
                val scaleFactor = minScale + (1 - minScale) * (1 - position)
                page.scaleX = scaleFactor
                page.scaleY = scaleFactor
            }
            else -> { // (1,+Infinity]
                // This page is way off-screen to the right.
                page.alpha = 0f
            }
        }
    }
}
