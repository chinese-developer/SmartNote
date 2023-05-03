@file:Suppress("unused")

package com.smarternote.core.ui.banner

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.RelativeLayout
import android.widget.RelativeLayout.LayoutParams
import androidx.annotation.ColorInt
import androidx.annotation.IntDef

class IndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), Indicator {

    private val interpolator: Interpolator = DecelerateInterpolator()
    private var accelerateInterpolator: Interpolator? = null
    private var path: Path? = null
    private var offset = 0f
    private var selectedPage = 0
    private var pagerCount = 0
    private var unColor = Color.GRAY
    private var selectedColor = Color.WHITE
    private val indicatorPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rectF: RectF = RectF()

    /**
     * 控制在banner中的位置
     */
    private var layoutParams: LayoutParams? = null

    /**
     * indicator样式
     */
    private var indicatorStyle = 0

    /*--------------- 核心控制点大小距离参数 ---------------*/
    private var indicatorRadius = dip2px(3.5f).toFloat()
    private var indicatorRatio = 1.0f
    private var indicatorSelectedRadius = dip2px(3.5f).toFloat()
    private var indicatorSelectedRatio = 1.0f
    private var indicatorSpacing = dip2px(10f).toFloat()

    private val ratioRadius: Float
        get() = indicatorRadius * indicatorRatio
    private val ratioSelectedRadius: Float
        get() = indicatorSelectedRadius * indicatorSelectedRatio

    @Retention
    @IntDef(
        IndicatorStyle.INDICATOR_CIRCLE,
        IndicatorStyle.INDICATOR_CIRCLE_RECT,
        IndicatorStyle.INDICATOR_BEZIER,
        IndicatorStyle.INDICATOR_DASH,
        IndicatorStyle.INDICATOR_BIG_CIRCLE
    )
    annotation class IndicatorStyle {
        companion object {
            const val INDICATOR_CIRCLE = 0
            const val INDICATOR_CIRCLE_RECT = 1
            const val INDICATOR_BEZIER = 2
            const val INDICATOR_DASH = 3
            const val INDICATOR_BIG_CIRCLE = 4
        }
    }

    override fun initIndicatorCount(pagerCount: Int) {
        this.pagerCount = pagerCount
        visibility = if (pagerCount > 1) VISIBLE else GONE
        requestLayout()
    }

    override fun getView(): View {
        return this
    }

    override fun getLayoutParams(): LayoutParams {
        if (layoutParams == null) {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                addRule(RelativeLayout.CENTER_HORIZONTAL)
                bottomMargin = dip2px(10f)
            }
        }
        return layoutParams!!
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        selectedPage = position
        offset = positionOffset
        invalidate()
    }

    override fun onPageSelected(position: Int) {}
    override fun onPageScrollStateChanged(state: Int) {}
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec))
    }

    private fun measureWidth(widthMeasureSpec: Int): Int {
        val mode = MeasureSpec.getMode(widthMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        var result = 0
        when (mode) {
            MeasureSpec.EXACTLY -> result = width
            MeasureSpec.AT_MOST, MeasureSpec.UNSPECIFIED -> {
                val ratioSelectedRadius = ratioSelectedRadius
                val ratioRadius = ratioRadius
                val diameterDistance = ratioSelectedRadius.coerceAtLeast(ratioRadius) * 2 * pagerCount
                val spacingDistance = (pagerCount - 1) * indicatorSpacing
                val al = ratioSelectedRadius - ratioRadius
                result = (diameterDistance + spacingDistance + al + paddingLeft + paddingRight).toInt()
            }

            else -> {}
        }
        return result
    }

    private fun measureHeight(heightMeasureSpec: Int): Int {
        val mode = MeasureSpec.getMode(heightMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        var result = 0
        when (mode) {
            MeasureSpec.EXACTLY -> result = height
            MeasureSpec.AT_MOST, MeasureSpec.UNSPECIFIED -> {
                val ratioSelectedRadius = ratioSelectedRadius
                val ratioRadius = ratioRadius
                val diameterDistance = ratioSelectedRadius.coerceAtLeast(ratioRadius) * 2
                result = (diameterDistance + paddingTop + paddingBottom).toInt()
            }

            else -> {}
        }
        return result
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (pagerCount == 0) {
            return
        }
        val midY = height / 2.0f + 0.5f
        when (indicatorStyle) {
            IndicatorStyle.INDICATOR_CIRCLE -> {
                drawCircle(canvas, midY)
            }
            IndicatorStyle.INDICATOR_CIRCLE_RECT -> {
                drawCircleRect(canvas, midY)
            }
            IndicatorStyle.INDICATOR_BEZIER -> {
                drawBezier(canvas, midY)
            }
            IndicatorStyle.INDICATOR_DASH -> {
                drawDash(canvas, midY)
            }
            IndicatorStyle.INDICATOR_BIG_CIRCLE -> {
                drawBigCircle(canvas, midY)
            }
        }
    }

    private fun drawCircle(canvas: Canvas, midY: Float) {
        drawPagerCountCircle(canvas, midY)
        val indicatorStartX = indicatorStartX(selectedPage)
        val nextIndicatorStartX = indicatorStartX((selectedPage + 1) % pagerCount)
        val ratioRadius = ratioSelectedRadius
        val left = indicatorStartX - ratioRadius
        val right = indicatorStartX + ratioRadius
        val nextLeft = nextIndicatorStartX - ratioRadius
        val nextRight = nextIndicatorStartX + ratioRadius
        val leftX = left + (nextLeft - left) * interpolatedOffset()
        val rightX = right + (nextRight - right) * interpolatedOffset()
        rectF[leftX, midY - indicatorSelectedRadius, rightX] = midY + indicatorSelectedRadius
        indicatorPaint.color = selectedColor
        canvas.drawRoundRect(rectF, indicatorSelectedRadius, indicatorSelectedRadius, indicatorPaint)
    }

    private fun drawCircleRect(canvas: Canvas, midY: Float) {
        drawPagerCountCircle(canvas, midY)
        val indicatorStartX = indicatorStartX(selectedPage)
        val ratioRadius = ratioSelectedRadius
        val left = indicatorStartX - ratioRadius
        val right = indicatorStartX + ratioRadius
        val offset = interpolatedOffset()
        var distance = indicatorSpacing + this.ratioRadius.coerceAtLeast(ratioRadius) * 2
        val leftX: Float
        val rightX: Float
        if ((selectedPage + 1) % pagerCount == 0) {
            distance *= -selectedPage.toFloat()
            leftX = left + (distance * offset * 2).coerceAtLeast(distance)
            rightX = right + (distance * (offset - 0.5f) * 2.0f).coerceAtMost(0f)
        } else {
            leftX = left + (distance * (offset - 0.5f) * 2.0f).coerceAtLeast(0f)
            rightX = right + (distance * offset * 2).coerceAtMost(distance)
        }
        rectF[leftX, midY - indicatorSelectedRadius, rightX] = midY + indicatorSelectedRadius
        indicatorPaint.color = selectedColor
        canvas.drawRoundRect(rectF, indicatorSelectedRadius, indicatorSelectedRadius, indicatorPaint)
    }

    private fun drawBezier(canvas: Canvas, midY: Float) {
        drawPagerCountCircle(canvas, midY)
        if (path == null) path = Path()
        if (accelerateInterpolator == null) accelerateInterpolator = AccelerateInterpolator()
        val indicatorStartX = indicatorStartX(selectedPage)
        val nextIndicatorStartX = indicatorStartX((selectedPage + 1) % pagerCount)
        val leftX = (indicatorStartX
                + (nextIndicatorStartX - indicatorStartX) * accelerateInterpolator!!.getInterpolation(offset))
        val rightX = indicatorStartX + (nextIndicatorStartX - indicatorStartX) * interpolatedOffset()
        val ratioSelectedRadius = ratioSelectedRadius
        val minRadius = indicatorSelectedRadius * 0.57f
        val minRatioRadius = minRadius * indicatorSelectedRatio
        val leftRadius = ratioSelectedRadius + (minRatioRadius - ratioSelectedRadius) * interpolatedOffset()
        val rightRadius = (minRatioRadius
                + (ratioSelectedRadius - minRatioRadius) * accelerateInterpolator!!.getInterpolation(offset))
        val leftTopOrBottomOffset = (indicatorSelectedRadius - minRadius) * interpolatedOffset()
        val rightTopOrBottomOffset = (indicatorSelectedRadius - minRadius) * accelerateInterpolator!!.getInterpolation(offset)
        indicatorPaint.color = selectedColor
        rectF[leftX - leftRadius, midY - indicatorSelectedRadius + leftTopOrBottomOffset, leftX + leftRadius] =
            midY + indicatorSelectedRadius - leftTopOrBottomOffset
        canvas.drawRoundRect(rectF, leftRadius, leftRadius, indicatorPaint)
        rectF[rightX - rightRadius, midY - minRadius - rightTopOrBottomOffset, rightX + rightRadius] = midY + minRadius + rightTopOrBottomOffset
        canvas.drawRoundRect(rectF, rightRadius, rightRadius, indicatorPaint)
        path!!.reset()
        path!!.moveTo(rightX, midY)
        path!!.lineTo(rightX, midY - minRadius - rightTopOrBottomOffset)
        path!!.quadTo(
            rightX + (leftX - rightX) / 2.0f, midY, leftX,
            midY - indicatorSelectedRadius + leftTopOrBottomOffset
        )
        path!!.lineTo(leftX, midY + indicatorSelectedRadius - leftTopOrBottomOffset)
        path!!.quadTo(
            rightX + (leftX - rightX) / 2.0f, midY, rightX,
            midY + minRadius + rightTopOrBottomOffset
        )
        path!!.close()
        canvas.drawPath(path!!, indicatorPaint)
    }

    private fun drawDash(canvas: Canvas, midY: Float) {
        val offset = interpolatedOffset()
        //默认dash的长度，设置ratio控制长度
        val ratioSelectedRadius = ratioSelectedRadius
        val ratioIndicatorRadius = ratioRadius
        val distance = ratioSelectedRadius - ratioIndicatorRadius
        val distanceOffset = distance * offset
        val nextPage = (selectedPage + 1) % pagerCount
        val isNextFirst = nextPage == 0
        indicatorPaint.color = unColor
        for (i in 0 until pagerCount) {
            var startCx = indicatorStartX(i)
            if (isNextFirst) startCx += distanceOffset
            val left = startCx - ratioIndicatorRadius
            val top = midY - indicatorRadius
            val right = startCx + ratioIndicatorRadius
            val bottom = midY + indicatorRadius
            if (selectedPage + 1 <= i) {
                rectF[left + distance, top, right + distance] = bottom
            } else {
                rectF[left, top, right] = bottom
            }
            canvas.drawRoundRect(rectF, indicatorRadius, indicatorRadius, indicatorPaint)
        }
        indicatorPaint.color = selectedColor
        if (offset < 0.99f) {
            var leftX = indicatorStartX(selectedPage) - ratioSelectedRadius
            if (isNextFirst) leftX += distanceOffset
            val rightX = leftX + ratioSelectedRadius * 2 + distance - distanceOffset
            rectF[leftX, midY - indicatorSelectedRadius, rightX] = midY + indicatorSelectedRadius
            canvas.drawRoundRect(rectF, indicatorSelectedRadius, indicatorSelectedRadius, indicatorPaint)
        }
        if (offset > 0.1f) {
            val nextRightX = indicatorStartX(nextPage) + ratioSelectedRadius + if (isNextFirst) distanceOffset else distance
            val nextLeftX = nextRightX - ratioSelectedRadius * 2 - distanceOffset
            rectF[nextLeftX, midY - indicatorSelectedRadius, nextRightX] = midY + indicatorSelectedRadius
            canvas.drawRoundRect(rectF, indicatorSelectedRadius, indicatorSelectedRadius, indicatorPaint)
        }
    }

    private fun drawBigCircle(canvas: Canvas, midY: Float) {
        drawPagerCountCircle(canvas, midY)
        val offset = interpolatedOffset()
        val indicatorStartX = indicatorStartX(selectedPage)
        val nextIndicatorStartX = indicatorStartX((selectedPage + 1) % pagerCount)
        val ratioRadius = ratioRadius
        val maxRadius = indicatorSelectedRadius
        val maxRatioRadius = maxRadius * indicatorSelectedRatio
        val leftRadius = maxRatioRadius - (maxRatioRadius - ratioRadius) * offset
        val rightRadius = ratioRadius + (maxRatioRadius - ratioRadius) * offset
        val topOrBottomOffset = (maxRadius - indicatorRadius) * offset
        indicatorPaint.color = selectedColor
        if (offset < 0.99f) {
            val top = midY - maxRadius + topOrBottomOffset
            val left = indicatorStartX - leftRadius
            val right = indicatorStartX + leftRadius
            val bottom = midY + maxRadius - topOrBottomOffset
            rectF[left, top, right] = bottom
            canvas.drawRoundRect(rectF, leftRadius, leftRadius, indicatorPaint)
        }
        if (offset > 0.1f) {
            val top = midY - indicatorRadius - topOrBottomOffset
            val left = nextIndicatorStartX - rightRadius
            val right = nextIndicatorStartX + rightRadius
            val bottom = midY + indicatorRadius + topOrBottomOffset
            rectF[left, top, right] = bottom
            canvas.drawRoundRect(rectF, rightRadius, rightRadius, indicatorPaint)
        }
    }

    private fun drawPagerCountCircle(canvas: Canvas, midY: Float) {
        indicatorPaint.color = unColor
        for (i in 0 until pagerCount) {
            val startCx = indicatorStartX(i)
            val ratioIndicatorRadius = ratioRadius
            val left = startCx - ratioIndicatorRadius
            val top = midY - indicatorRadius
            val right = startCx + ratioIndicatorRadius
            val bottom = midY + indicatorRadius
            rectF[left, top, right] = bottom
            canvas.drawRoundRect(rectF, indicatorRadius, indicatorRadius, indicatorPaint)
        }
    }

    private fun indicatorStartX(index: Int): Float {
        val ratioRadius = ratioRadius
        val ratioSelectedRadius = ratioSelectedRadius
        val ratioIndicatorRadius = ratioRadius.coerceAtLeast(ratioSelectedRadius)
        val centerSpacing = ratioIndicatorRadius * 2.0f + indicatorSpacing
        val centerX = ratioIndicatorRadius + paddingLeft + centerSpacing * index
        /**
         * 为了适配INDICATOR_DASH样式， measure 中默认多增加了 ratioIndicatorRadius - ratioRadius 的高度和宽度
         * 除了INDICATOR_DASH样式下，其他样式需要增加indicatorSelectedRadius一半的距离，让位置居中。
         */
        return centerX + if (indicatorStyle == IndicatorStyle.INDICATOR_DASH) 0F else (ratioIndicatorRadius - ratioRadius) / 2
    }

    private fun interpolatedOffset(): Float {
        return interpolator.getInterpolation(offset)
    }

    private fun dip2px(dp: Float): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    /*--------------- 下面是暴露的方法 ---------------*/
    /**
     * 设置indicator的圆角，同时会改变选中时的圆角，default 3.5dp
     *
     * @param indicatorRadius 单位dp
     */
    fun setIndicatorRadius(indicatorRadius: Float): IndicatorView {
        val indicatorRadiusDp = dip2px(indicatorRadius)
        if (this.indicatorRadius == indicatorSelectedRadius) {
            indicatorSelectedRadius = indicatorRadiusDp.toFloat()
        }
        this.indicatorRadius = indicatorRadiusDp.toFloat()
        return this
    }

    /**
     * 设置indicator比例，拉伸圆为矩形，控制该比例，default 1.0
     * [IndicatorView.ratioRadius]
     *
     * @param indicatorRatio indicatorRadius * indicatorRatio
     */
    fun setIndicatorRatio(indicatorRatio: Float): IndicatorView {
        if (this.indicatorRatio == indicatorSelectedRatio) {
            indicatorSelectedRatio = indicatorRatio
        }
        this.indicatorRatio = indicatorRatio
        return this
    }

    /**
     * 设置选中的圆角，没有设置，默认和indicatorRadius值一致
     *
     * @param indicatorSelectedRadius 单位dp
     */
    fun setIndicatorSelectedRadius(indicatorSelectedRadius: Float): IndicatorView {
        this.indicatorSelectedRadius = dip2px(indicatorSelectedRadius).toFloat()
        return this
    }

    /**
     * 设置选中圆比例，拉伸圆为矩形，控制该比例，默认比例和indicatorRatio一致
     *
     * @param indicatorSelectedRatio indicatorSelectedRadius * indicatorSelectedRatio
     */
    fun setIndicatorSelectedRatio(indicatorSelectedRatio: Float): IndicatorView {
        this.indicatorSelectedRatio = indicatorSelectedRatio
        return this
    }

    /**
     * 设置点与点之间的距离，default 10dp
     *
     * @param indicatorSpacing 单位dp
     */
    fun setIndicatorSpacing(indicatorSpacing: Float): IndicatorView {
        this.indicatorSpacing = dip2px(indicatorSpacing).toFloat()
        return this
    }

    fun setIndicatorStyle(@IndicatorStyle indicatorStyle: Int): IndicatorView {
        this.indicatorStyle = indicatorStyle
        return this
    }

    fun setIndicatorColor(@ColorInt indicatorColor: Int): IndicatorView {
        unColor = indicatorColor
        return this
    }

    fun setIndicatorSelectorColor(@ColorInt indicatorSelectorColor: Int): IndicatorView {
        selectedColor = indicatorSelectorColor
        return this
    }

    fun setLayoutParams(params: LayoutParams): IndicatorView {
        this.layoutParams = params
        return this
    }
}