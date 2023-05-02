@file:Suppress("DEPRECATED_IDENTITY_EQUALS")

package com.smarternote.core.ui.indicator

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.animation.Interpolator
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import com.smarternote.core.R

open class BaseCircleIndicator constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    protected var mIndicatorMargin = -1
    protected var mIndicatorWidth = -1
    protected var mIndicatorHeight = -1
    protected var mIndicatorBackgroundResId = 0
    protected var mIndicatorUnselectedBackgroundResId = 0
    protected lateinit var mIndicatorTintColor: ColorStateList
    protected lateinit var mIndicatorTintUnselectedColor: ColorStateList
    protected lateinit var mAnimatorOut: Animator
    protected lateinit var mAnimatorIn: Animator
    protected lateinit var mImmediateAnimatorOut: Animator
    protected lateinit var mImmediateAnimatorIn: Animator
    protected var mLastPosition = -1
    private var mIndicatorCreatedListener: IndicatorCreatedListener? = null

    init {
        val config: IndicatorConfig = handleTypedArray(context, attrs)
        initialize(config)
        if (isInEditMode) {
            createIndicators(3, 1)
        }
    }
    private fun handleTypedArray(context: Context, attrs: AttributeSet?): IndicatorConfig {
        val config = IndicatorConfig()
        if (attrs == null) {
            return config
        }
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseCircleIndicator)
        config.width = typedArray.getDimensionPixelSize(R.styleable.BaseCircleIndicator_ci_width, -1)
        config.height = typedArray.getDimensionPixelSize(R.styleable.BaseCircleIndicator_ci_height, -1)
        config.margin = typedArray.getDimensionPixelSize(R.styleable.BaseCircleIndicator_ci_margin, -1)
        config.animatorResId = typedArray.getResourceId(
            R.styleable.BaseCircleIndicator_ci_animator,
            R.animator.indicator_scale_with_alpha
        )
        config.animatorReverseResId = typedArray.getResourceId(R.styleable.BaseCircleIndicator_ci_animator_reverse, 0)
        config.backgroundResId = typedArray.getResourceId(
            R.styleable.BaseCircleIndicator_ci_drawable,
            R.drawable.indicator_white_radius
        )
        config.unselectedBackgroundId = typedArray.getResourceId(
            R.styleable.BaseCircleIndicator_ci_drawable_unselected,
            config.backgroundResId
        )
        config.orientation = typedArray.getInt(R.styleable.BaseCircleIndicator_ci_orientation, -1)
        config.gravity = typedArray.getInt(R.styleable.BaseCircleIndicator_ci_gravity, -1)
        typedArray.recycle()
        return config
    }

    fun initialize(config: IndicatorConfig) {
        val miniSize = (TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            DEFAULT_INDICATOR_WIDTH.toFloat(), resources.displayMetrics
        ) + 0.5f).toInt()
        mIndicatorWidth = if (config.width < 0) miniSize else config.width
        mIndicatorHeight = if (config.height < 0) miniSize else config.height
        mIndicatorMargin = if (config.margin < 0) miniSize else config.margin
        mAnimatorOut = createAnimatorOut(config)
        mImmediateAnimatorOut = createAnimatorOut(config)
        mImmediateAnimatorOut.duration = 0
        mAnimatorIn = createAnimatorIn(config)
        mImmediateAnimatorIn = createAnimatorIn(config)
        mImmediateAnimatorIn.duration = 0
        mIndicatorBackgroundResId = if (config.backgroundResId === 0) R.drawable.indicator_white_radius else config.backgroundResId
        mIndicatorUnselectedBackgroundResId = if (config.unselectedBackgroundId === 0) config.backgroundResId else config.unselectedBackgroundId
        orientation = if (config.orientation === VERTICAL) VERTICAL else HORIZONTAL
        gravity = if (config.gravity >= 0) config.gravity else Gravity.CENTER
    }

    @JvmOverloads
    fun tintIndicator(
        @ColorInt indicatorColor: Int,
        @ColorInt unselectedIndicatorColor: Int = indicatorColor
    ) {
        mIndicatorTintColor = ColorStateList.valueOf(indicatorColor)
        mIndicatorTintUnselectedColor = ColorStateList.valueOf(unselectedIndicatorColor)
        changeIndicatorBackground()
    }

    @JvmOverloads
    fun changeIndicatorResource(
        @DrawableRes indicatorResId: Int,
        @DrawableRes indicatorUnselectedResId: Int = indicatorResId
    ) {
        mIndicatorBackgroundResId = indicatorResId
        mIndicatorUnselectedBackgroundResId = indicatorUnselectedResId
        changeIndicatorBackground()
    }

    interface IndicatorCreatedListener {
        /**
         * IndicatorCreatedListener
         *
         * @param view internal indicator view
         * @param position position
         */
        fun onIndicatorCreated(view: View?, position: Int)
    }

    fun setIndicatorCreatedListener(
        indicatorCreatedListener: IndicatorCreatedListener?
    ) {
        mIndicatorCreatedListener = indicatorCreatedListener
    }

    protected fun createAnimatorOut(config: IndicatorConfig): Animator {
        return AnimatorInflater.loadAnimator(context, config.animatorResId)
    }

    protected fun createAnimatorIn(config: IndicatorConfig): Animator {
        val animatorIn: Animator
        if (config.animatorReverseResId === 0) {
            animatorIn = AnimatorInflater.loadAnimator(context, config.animatorResId)
            animatorIn.interpolator = ReverseInterpolator()
        } else {
            animatorIn = AnimatorInflater.loadAnimator(context, config.animatorReverseResId)
        }
        return animatorIn
    }

    fun createIndicators(count: Int, currentPosition: Int) {
        if (mImmediateAnimatorOut.isRunning) {
            mImmediateAnimatorOut.end()
            mImmediateAnimatorOut.cancel()
        }
        if (mImmediateAnimatorIn.isRunning) {
            mImmediateAnimatorIn.end()
            mImmediateAnimatorIn.cancel()
        }

        // Diff View
        val childViewCount = childCount
        if (count < childViewCount) {
            removeViews(count, childViewCount - count)
        } else if (count > childViewCount) {
            val addCount = count - childViewCount
            val orientation = orientation
            for (i in 0 until addCount) {
                addIndicator(orientation)
            }
        }

        // Bind Style
        var indicator: View
        for (i in 0 until count) {
            indicator = getChildAt(i)
            if (currentPosition == i) {
                bindIndicatorBackground(indicator, mIndicatorBackgroundResId, mIndicatorTintColor)
                mImmediateAnimatorOut.setTarget(indicator)
                mImmediateAnimatorOut.start()
                mImmediateAnimatorOut.end()
            } else {
                bindIndicatorBackground(
                    indicator, mIndicatorUnselectedBackgroundResId,
                    mIndicatorTintUnselectedColor
                )
                mImmediateAnimatorIn.setTarget(indicator)
                mImmediateAnimatorIn.start()
                mImmediateAnimatorIn.end()
            }
            mIndicatorCreatedListener?.onIndicatorCreated(indicator, i)
        }
        mLastPosition = currentPosition
    }

    protected fun addIndicator(orientation: Int) {
        val indicator = View(context)
        val params = generateDefaultLayoutParams()
        params.width = mIndicatorWidth
        params.height = mIndicatorHeight
        if (orientation == HORIZONTAL) {
            params.leftMargin = mIndicatorMargin
            params.rightMargin = mIndicatorMargin
        } else {
            params.topMargin = mIndicatorMargin
            params.bottomMargin = mIndicatorMargin
        }
        addView(indicator, params)
    }

    fun animatePageSelected(position: Int) {
        if (mLastPosition == position) {
            return
        }
        if (mAnimatorIn.isRunning) {
            mAnimatorIn.end()
            mAnimatorIn.cancel()
        }
        if (mAnimatorOut.isRunning) {
            mAnimatorOut.end()
            mAnimatorOut.cancel()
        }
        var currentIndicator: View? = null
        if (mLastPosition >= 0 && getChildAt(mLastPosition).also { currentIndicator = it } != null) {
            bindIndicatorBackground(
                currentIndicator!!, mIndicatorUnselectedBackgroundResId,
                mIndicatorTintUnselectedColor
            )
            mAnimatorIn.setTarget(currentIndicator)
            mAnimatorIn.start()
        }
        val selectedIndicator = getChildAt(position)
        if (selectedIndicator != null) {
            bindIndicatorBackground(
                selectedIndicator, mIndicatorBackgroundResId,
                mIndicatorTintColor
            )
            mAnimatorOut.setTarget(selectedIndicator)
            mAnimatorOut.start()
        }
        mLastPosition = position
    }

    protected fun changeIndicatorBackground() {
        val count = childCount
        if (count <= 0) {
            return
        }
        var currentIndicator: View
        for (i in 0 until count) {
            currentIndicator = getChildAt(i)
            if (i == mLastPosition) {
                bindIndicatorBackground(
                    currentIndicator, mIndicatorBackgroundResId,
                    mIndicatorTintColor
                )
            } else {
                bindIndicatorBackground(
                    currentIndicator, mIndicatorUnselectedBackgroundResId,
                    mIndicatorTintUnselectedColor
                )
            }
        }
    }

    private fun bindIndicatorBackground(
        view: View,
        @DrawableRes drawableRes: Int,
        tintColor: ColorStateList?
    ) {
        if (tintColor != null) {
            val indicatorDrawable = DrawableCompat.wrap(
                ContextCompat.getDrawable(context, drawableRes)!!.mutate()
            )
            DrawableCompat.setTintList(indicatorDrawable, tintColor)
            ViewCompat.setBackground(view, indicatorDrawable)
        } else {
            view.setBackgroundResource(drawableRes)
        }
    }

    protected class ReverseInterpolator : Interpolator {
        override fun getInterpolation(value: Float): Float {
            return Math.abs(1.0f - value)
        }
    }

    companion object {
        private const val DEFAULT_INDICATOR_WIDTH = 5
    }
}