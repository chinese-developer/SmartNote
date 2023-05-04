@file:Suppress("NotifyDataSetChanged", "UNCHECKED_CAST", "unused")

package com.smarternote.core.ui.banner

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs
import kotlin.math.max


class Banner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : RelativeLayout(context, attrs, defStyleAttr) {

    var currentPageSelectedPosition = 0
    private var pageFlingDuration = 800
    private var aspectRatio = 16f / 9f
    private var turningNextPageDuration = 4000L
    private var indicator: Indicator? = null
    private var onPageChangeCallback: ViewPager2.OnPageChangeCallback? = null

    companion object {
        /**
         * 滑动距离的阈值
         */
        private const val SCALED_TOUCH_SLOP = 8

    }


    private val viewPager: ViewPager2
    private val adapter: WrapperAdapter
    private val handler = Handler(Looper.getMainLooper())
    private val compositePagetransformer: CompositePageTransformer

    private var autoPlayRunnable = getAutoPlayRunnable()
    private var autoPlay = false

    private var realItemCount = 0

    init {
        buildDefaultIndicator()
        adapter = WrapperAdapter()
        viewPager = ViewPager2(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            offscreenPageLimit = 1
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            compositePagetransformer = CompositePageTransformer()
            setPageTransformer(compositePagetransformer)
            registerOnPageChangeCallback(OnPageChangeCallback())
        }
        slowFlingRecyclerView(viewPager)
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
            if (isAutoPlay()) {
                currentPageSelectedPosition++
                if (currentPageSelectedPosition >= realItemCount) {
                    viewPager.setCurrentItem(0, false)
                    currentPageSelectedPosition = 0
                    handler.postDelayed(this, turningNextPageDuration)
                } else {
                    viewPager.setCurrentItem(currentPageSelectedPosition, true)
                    handler.postDelayed(this, turningNextPageDuration)
                }
            }
        }
    }

    @JvmOverloads
    fun build(startPosition: Int = 0) {
        resetPagerItemCount()
        indicator?.initIndicatorCount(adapter.itemCount)
        viewPager.adapter?.notifyDataSetChanged() ?: kotlin.run {
            viewPager.adapter = adapter
        }
        currentPageSelectedPosition = startPosition
        viewPager.isUserInputEnabled = realItemCount > 1
        viewPager.setCurrentItem(currentPageSelectedPosition, false)
        indicator?.initIndicatorCount(realItemCount)
        startPolling()
    }

    fun setAdapter(adapter: RecyclerView.Adapter<out ViewHolder>): Banner {
        this.adapter.register(adapter)
        return this
    }

    fun setTurningNextPageDuration(turningNextPageDuration: Long): Banner {
        this.turningNextPageDuration = turningNextPageDuration
        return this
    }

    fun setPageFlingDuration(pageFlingDuration: Int): Banner {
        this.pageFlingDuration = pageFlingDuration
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

    fun getCurrentPosition(): Int {
        val position = getRealPageSelectedPosition(currentPageSelectedPosition)
        return max(position , 0)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (autoPlay && realItemCount > 1) {
            startPolling()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopPolling()
    }

    private fun startPolling() {
        stopPolling()
        handler.postDelayed(autoPlayRunnable, turningNextPageDuration)
    }

    private fun stopPolling() {
        handler.removeCallbacks(autoPlayRunnable)
    }

    private fun isAutoPlay(): Boolean = autoPlay && realItemCount > 1

    private fun resetPagerItemCount() {
        val externalAdapter = adapter.getExternalAdapter()
        realItemCount = if (externalAdapter == null || externalAdapter.itemCount == 0) 0 else  adapter.itemCount
    }

    private fun getRealPageSelectedPosition(position: Int): Int {
        var realPosition = 0
        if (realItemCount != 0) {
            realPosition = position % realItemCount
        }
        if (realPosition < 0) {
            realPosition += realItemCount
        }
        return realPosition
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
        if (isAutoPlay() && viewPager.isUserInputEnabled) {
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

    inner class OnPageChangeCallback : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            val realPageSelectedPosition = getRealPageSelectedPosition(position)
            onPageChangeCallback?.onPageScrolled(realPageSelectedPosition, positionOffset, positionOffsetPixels)
            indicator?.onPageScrolled(realPageSelectedPosition, positionOffset, positionOffsetPixels)
        }

        override fun onPageSelected(position: Int) {
            currentPageSelectedPosition = position
            val realPageSelectedPosition = getRealPageSelectedPosition(position)
            onPageChangeCallback?.onPageSelected(realPageSelectedPosition)
            indicator?.onPageSelected(realPageSelectedPosition)
        }

        override fun onPageScrollStateChanged(state: Int) {
            onPageChangeCallback?.onPageScrollStateChanged(state)
            indicator?.onPageScrollStateChanged(state)
            if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                if (currentPageSelectedPosition == realItemCount) {
                    viewPager.setCurrentItem(0, false)
                } else if (currentPageSelectedPosition < 0) {
                    viewPager.setCurrentItem(realItemCount + currentPageSelectedPosition, false)
                }
            }
        }
    }

    inner class WrapperAdapter : RecyclerView.Adapter<ViewHolder>() {

        init {
          setHasStableIds(true)
        }

        private lateinit var externalAdapter: RecyclerView.Adapter<ViewHolder>

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return externalAdapter.onCreateViewHolder(parent, viewType)
        }

        override fun getItemCount(): Int {
            return if (::externalAdapter.isInitialized) externalAdapter.itemCount else 0
        }

        override fun getItemViewType(position: Int): Int {
            return if (::externalAdapter.isInitialized) externalAdapter.getItemViewType(getRealPageSelectedPosition(position)) else -1
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            return externalAdapter.onBindViewHolder(holder, getRealPageSelectedPosition(position))
        }

        fun register(adapter: RecyclerView.Adapter<out ViewHolder>) {
            if (getExternalAdapter() != null) {
                externalAdapter.unregisterAdapterDataObserver(adapterDataObserver)
            }
            externalAdapter = adapter as RecyclerView.Adapter<ViewHolder>
            externalAdapter.registerAdapterDataObserver(adapterDataObserver)
        }

        fun getExternalAdapter(): RecyclerView.Adapter<ViewHolder>? = if (::externalAdapter.isInitialized) externalAdapter else null
    }

    val adapterDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            onChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            onChanged()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            onChanged()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            onChanged()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            onChanged()
        }

        override fun onChanged() {
            resetPagerItemCount()
            val startPosition = getCurrentPosition()
            build(startPosition)
        }
    }

    inner class SlowFlingLayoutManager(
        context: Context,
        private val layoutManager: LinearLayoutManager
    ) : LinearLayoutManager(context, layoutManager.orientation, false) {

        /**
         * `performAccessibilityAction`，`onInitializeAccessibilityNodeInfo`，`requestChildRectangleOnScreen`
         * 这三个方法的作用都是将 RecyclerView 的可访问性和可视化状态委托给外部传递的 LinearLayoutManager。
         */
        override fun performAccessibilityAction(
            recycler: RecyclerView.Recycler,
            state: RecyclerView.State,
            action: Int,
            args: Bundle?
        ): Boolean {
            return layoutManager.performAccessibilityAction(recycler, state, action, args)
        }

        override fun onInitializeAccessibilityNodeInfo(
            recycler: RecyclerView.Recycler,
            state: RecyclerView.State,
            info: AccessibilityNodeInfoCompat
        ) {
            layoutManager.onInitializeAccessibilityNodeInfo(recycler, state, info)
        }

        override fun requestChildRectangleOnScreen(
            parent: RecyclerView,
            child: View,
            rect: Rect,
            immediate: Boolean,
            focusedChildVisible: Boolean
        ): Boolean {
            return layoutManager.requestChildRectangleOnScreen(parent, child, rect, immediate, focusedChildVisible)
        }

        override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State, position: Int) {
            val linearSmoothScroller = object : LinearSmoothScroller(recyclerView.context) {
                override fun calculateTimeForDeceleration(dx: Int): Int {
                    // 计算惯性滚动的时间。
                    return (pageFlingDuration * (1 - 0.3356)).toInt()
                }
            }
            linearSmoothScroller.targetPosition = position
            startSmoothScroll(linearSmoothScroller)
        }

        override fun calculateExtraLayoutSpace(state: RecyclerView.State, extraLayoutSpace: IntArray) {
            // 计算额外的布局空间。
            val pageLimit = viewPager.offscreenPageLimit
            if (pageLimit == ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT) {
                super.calculateExtraLayoutSpace(state, extraLayoutSpace)
                return
            }
            val offscreenSpace = getPageSize() * pageLimit
            extraLayoutSpace[0] = offscreenSpace
            extraLayoutSpace[1] = offscreenSpace
        }

        private fun getPageSize(): Int {
            // 获取每一页的大小。
            val rv = viewPager.getChildAt(0) as RecyclerView
            return if (orientation == RecyclerView.HORIZONTAL) {
                rv.width - rv.paddingLeft - rv.paddingRight
            } else {
                rv.height - rv.paddingTop - rv.paddingBottom
            }
        }
    }

    /**
     * 用于设置 RecyclerView 的滚动行为以解决过度滚动的问题。
     */
    private fun slowFlingRecyclerView(viewPager2: ViewPager2) {
        try {
            val recyclerView = viewPager2.getChildAt(0) as RecyclerView
            // 禁用 RecyclerView 的过度滚动效果。
            recyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            val originalLayoutManager = recyclerView.layoutManager as LinearLayoutManager
            val proxyLayoutManager = SlowFlingLayoutManager(viewPager2.context, originalLayoutManager)
            // 使用代理的 SlowFlingLayoutManager，这时候 originalLayoutManager 上的 mRecyclerView 会被置空。
            recyclerView.layoutManager = proxyLayoutManager

            // 由于设置了代理 SlowFlingLayoutManager，但是内部方法调用上还是使用的 originalLayoutManager 实现的方法
            // 为了避免空指针，这里将 originalLayoutManager 塞回去
            RecyclerView.LayoutManager::class.java.getDeclaredField("mRecyclerView").apply {
                isAccessible = true
                set(originalLayoutManager, recyclerView)
            }

            ViewPager2::class.java.getDeclaredField("mLayoutManager").apply {
                isAccessible = true
                set(viewPager2, proxyLayoutManager)
            }

            listOf("mScrollEventAdapter", "mPageTransformerAdapter").forEach { fieldName ->
                ViewPager2::class.java.getDeclaredField(fieldName).apply {
                    // 设置私有字段的可访问性。
                    isAccessible = true
                    val fieldValue = get(viewPager2)
                    // 通过反射获取 ViewPager2 的私有字段。
                    fieldValue?.javaClass?.getDeclaredField("mLayoutManager")?.apply {
                        isAccessible = true
                        // 将自定义的 RecyclerView 布局管理器设置为 mLayoutManager 字段的值。
                        set(fieldValue, proxyLayoutManager)
                    }
                }
            }
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

}
