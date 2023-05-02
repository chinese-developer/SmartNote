package com.smarternote.core.ui.indicator

import android.view.Gravity
import android.widget.LinearLayout
import androidx.annotation.AnimatorRes
import androidx.annotation.DrawableRes
import com.smarternote.core.R

class IndicatorConfig internal constructor() {

    var width = -1
    var height = -1
    var margin = -1

    @AnimatorRes
    var animatorResId: Int = R.animator.indicator_scale_with_alpha

    @AnimatorRes
    var animatorReverseResId = 0

    @DrawableRes
    var backgroundResId: Int = R.drawable.indicator_white_radius

    @DrawableRes
    var unselectedBackgroundId = 0
    var orientation = LinearLayout.HORIZONTAL
    var gravity = Gravity.CENTER

    class Builder {

        private val mConfig: IndicatorConfig = IndicatorConfig()

        fun width(width: Int): Builder {
            mConfig.width = width
            return this
        }

        fun height(height: Int): Builder {
            mConfig.height = height
            return this
        }

        fun margin(margin: Int): Builder {
            mConfig.margin = margin
            return this
        }

        fun animator(@AnimatorRes animatorResId: Int): Builder {
            mConfig.animatorResId = animatorResId
            return this
        }

        fun animatorReverse(@AnimatorRes animatorReverseResId: Int): Builder {
            mConfig.animatorReverseResId = animatorReverseResId
            return this
        }

        fun drawable(@DrawableRes backgroundResId: Int): Builder {
            mConfig.backgroundResId = backgroundResId
            return this
        }

        fun drawableUnselected(@DrawableRes unselectedBackgroundId: Int): Builder {
            mConfig.unselectedBackgroundId = unselectedBackgroundId
            return this
        }

        fun orientation(orientation: Int): Builder {
            mConfig.orientation = orientation
            return this
        }

        fun gravity(gravity: Int): Builder {
            mConfig.gravity = gravity
            return this
        }

        fun build(): IndicatorConfig {
            return mConfig
        }
    }
}