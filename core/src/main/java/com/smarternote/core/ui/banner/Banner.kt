@file:Suppress("NotifyDataSetChanged", "UNCHECKED_CAST", "unused")

package com.smarternote.core.ui.banner

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs


class Banner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val viewPager: ViewPager2
    private val adapter: WrapperAdapter
    private val handler = Handler(Looper.getMainLooper())
    private val compositePagetransformer: CompositePageTransformer

    private var isPollingStarted = false
    private var autoPlayRunnable = getAutoPlayRunnable()
    private var autoPlay = true

    var currentPage = 0
    private var speedFactor = 0.5f
    private var aspectRatio = 16f / 9f
    private var autoTurningTime = 4000L
    private var indicator: Indicator? = null
    private var onPageChangeCallback: ViewPager2.OnPageChangeCallback? = null

    companion object {
        /**
         * 滑动距离的阈值
         */
        private const val SCALED_TOUCH_SLOP = 8

    }

    init {
        buildDefaultIndicator()
        adapter = WrapperAdapter()
        viewPager = ViewPager2(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            offscreenPageLimit = 1
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            compositePagetransformer = CompositePageTransformer()
            compositePagetransformer.addTransformer(slowScrollPageTransformer)
            setPageTransformer(compositePagetransformer)
            registerOnPageChangeCallback(OnPageChangeCallback())
            adapter = this@Banner.adapter
        }
        addView(viewPager)

//        viewPager.setPageTransformer { page, position ->
//            val absPos = abs(position)
//            page.scaleY = (1 - absPos) * 0.15f + 0.85f
//            page.alpha = 1 - absPos
//            page.translationX = -page.width * position
//            page.translationY = 30 * absPos
//        }
    }

    private fun buildDefaultIndicator() {
        indicator = IndicatorView(context)
            .setIndicatorRatio(4f)
            .setIndicatorRadius(2f)
            .setIndicatorSelectedRatio(6f)
            .setIndicatorSelectedRadius(2f)
            .setIndicatorSpacing(0f)
            .setIndicatorStyle(IndicatorView.IndicatorStyle.INDICATOR_CIRCLE_RECT)
            .setIndicatorColor(Color.parseColor("#1AFFFFFF"))
            .setIndicatorSelectorColor(Color.parseColor("#99FFFFFF")).apply {
                this@Banner.addView(getView(), layoutParams)
            }
    }

    private fun getAutoPlayRunnable() = object : Runnable {
        override fun run() {
            if (adapter.itemCount > 0) {
                viewPager.currentItem = viewPager.currentItem + 1
            }
            handler.postDelayed(this, autoTurningTime)
        }
    }

    fun build() {
        indicator?.initIndicatorCount(adapter.itemCount)
        startPolling()
    }

    fun setAdapter(adapter: RecyclerView.Adapter<out ViewHolder>): Banner {
        this.adapter.register(adapter)
        this.adapter.notifyDataSetChanged()
        return this
    }

    fun setAutoTurningTime(autoTurningTime: Long): Banner {
        this.autoTurningTime = autoTurningTime
        return this
    }

    fun setSpeedFactor(autoTurningTime: Long): Banner {
        this.autoTurningTime = autoTurningTime
        return this
    }

    fun setPageTransformer(transformer: ViewPager2.PageTransformer): Banner {
        viewPager.setPageTransformer(transformer)
        return this
    }

    fun setIndicator(indicator: Indicator?): Banner {
        removeView(this.indicator?.getView())
        this.indicator = indicator
        if (indicator != null) {
            addView(indicator.getView(), indicator.getLayoutParams())
        }
        return this
    }

    fun setOnpageChangeCallback(onPageChangeCallback: ViewPager2.OnPageChangeCallback): Banner {
        this.onPageChangeCallback = onPageChangeCallback
        return this
    }

    fun setAspectRatio(ratio: Float): Banner {
        aspectRatio = ratio
        return this
    }

    fun setAutoPlay(enable: Boolean): Banner {
        autoPlay = enable
        if (enable) {
            startPolling()
        } else {
            stopPolling()
        }
        return this
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (autoPlay) {
            startPolling()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopPolling()
    }

    private fun startPolling() {
        if (isPollingStarted) {
            return
        }
        isPollingStarted = true
        handler.postDelayed(autoPlayRunnable, autoTurningTime)
    }

    private fun stopPolling() {
        isPollingStarted = false
        handler.removeCallbacks(autoPlayRunnable)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        if (widthMode == MeasureSpec.EXACTLY) {
            val heightSize = (widthSize / aspectRatio).toInt()
            val newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY)
            super.onMeasure(widthMeasureSpec, newHeightMeasureSpec)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (autoPlay && viewPager.isUserInputEnabled) {
            when (ev?.action) {
                MotionEvent.ACTION_DOWN -> stopPolling()
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE -> startPolling()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private var startX = 0f
    private var lastX = 0f
    private var startY = 0f
    private var lastY = 0f
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                // 当手指按下时，记录初始位置信息。
                startX = ev.rawX
                lastX = startX
                startY = ev.rawY
                lastY = startY
            }

            MotionEvent.ACTION_MOVE -> {
                // 当手指移动时，根据 ViewPager2 的方向计算水平或垂直方向上的滑动距离，根据一定的阈值判断是否需要禁止父布局拦截事件。
                lastX = ev.rawX
                lastY = ev.rawY
                if (viewPager.isUserInputEnabled) {
                    val distanceX = abs(lastX - startX)
                    val distanceY = abs(lastY - startY)
                    val disallowIntercept = if (viewPager.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                        distanceX > SCALED_TOUCH_SLOP && distanceX > distanceY
                    } else {
                        distanceY > SCALED_TOUCH_SLOP && distanceY > distanceX
                    }
                    parent.requestDisallowInterceptTouchEvent(disallowIntercept)
                }
            }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                // 当手指抬起或取消时，判断是否发生了足够的滑动距离，如果滑动距离超过一定的阈值，则认为需要拦截事件，否则交给子视图进行处理。
                return abs(lastX - startX) > SCALED_TOUCH_SLOP || abs(lastY - startY) > SCALED_TOUCH_SLOP
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    private val slowScrollPageTransformer = ViewPager2.PageTransformer { page, position ->
        page.translationX = -page.width * position * speedFactor
    }

    inner class OnPageChangeCallback : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            onPageChangeCallback?.onPageScrolled(position, positionOffset, positionOffsetPixels)
            indicator?.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageSelected(position: Int) {
            currentPage = position
            onPageChangeCallback?.onPageSelected(position)
            indicator?.onPageSelected(position)
        }

        override fun onPageScrollStateChanged(state: Int) {
            onPageChangeCallback?.onPageScrollStateChanged(state)
            indicator?.onPageScrollStateChanged(state)
            if (state == ViewPager2.SCROLL_STATE_IDLE) {
                val itemCount = adapter.itemCount
                if (currentPage == 0) {
                    viewPager.setCurrentItem(itemCount - 1, false)
                } else if (currentPage == itemCount - 1) {
                    viewPager.setCurrentItem(0, false)
                }
            }
        }
    }

    inner class WrapperAdapter : RecyclerView.Adapter<ViewHolder>() {

        private lateinit var externalAdapter: RecyclerView.Adapter<ViewHolder>

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return externalAdapter.onCreateViewHolder(parent, viewType)
        }

        override fun getItemCount(): Int {
            return if (::externalAdapter.isInitialized) externalAdapter.itemCount else 0
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            return externalAdapter.onBindViewHolder(holder, position)
        }

        fun register(adapter: RecyclerView.Adapter<out ViewHolder>) {
            this.externalAdapter = adapter as RecyclerView.Adapter<ViewHolder>
        }
    }
}
