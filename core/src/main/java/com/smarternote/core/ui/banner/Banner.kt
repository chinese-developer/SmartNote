@file:Suppress("NotifyDataSetChanged", "UNCHECKED_CAST", "unused")

package com.smarternote.core.ui.banner

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.PageTransformer
import com.smarternote.core.ui.viewpager.transformer.DepthPageTransformation
import kotlin.math.abs
import kotlin.math.max


class Banner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : RelativeLayout(context, attrs, defStyleAttr), DefaultLifecycleObserver {

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
    private val compositePagetransformer: CompositePageTransformer
    private val defaultTransformer by lazy { DepthPageTransformation() }

    private var autoPlay = false
    private var isPollingStarted = false
    private var lifecycleOwner: LifecycleOwner? = null

    /**
     * Current page 的范围是 [1-size], 而不是从 0 开始
     * 这是为了当展示第0页数据时, 可以右滑返回最后一页数据.
     * [realPagePosition] 是从 0 开始真实的数据下标值.
     */
    private var currentPagePosition = 0
    private var realPagePosition = 0
    private var draggingExtraPageCount = 0 // 模拟无限轮播，当手指滑动从第一页可以回退到最后一页，最后一页数据时可以滑动到第一页

    init {
        adapter = WrapperAdapter()
        viewPager = ViewPager2(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            compositePagetransformer = CompositePageTransformer()
            compositePagetransformer.addTransformer(defaultTransformer)
            setPageTransformer(compositePagetransformer)
            registerOnPageChangeCallback(OnPageChangeCallback())
        }
        slowFlingRecyclerView(viewPager)
        addView(viewPager)
        buildDefaultIndicator()
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

    private val autoPlayRunnable = object : Runnable {
        override fun run() {
            if (isAutoPlay()) {
                currentPagePosition++
                if (currentPagePosition == realPagePosition + 2) {
                    viewPager.setCurrentItem(1, false)
                    post(this)
                } else {
                    viewPager.setCurrentItem(currentPagePosition, true)
                    postDelayed(this, turningNextPageDuration)
                }
            }
        }
    }

    @JvmOverloads
    fun build(startPosition: Int = 0) {
        resetPagerCount()
        viewPager.adapter?.notifyDataSetChanged() ?: kotlin.run {
            viewPager.adapter = adapter
        }
        currentPagePosition = startPosition + 1
        viewPager.isUserInputEnabled = realPagePosition > 1
        viewPager.setCurrentItem(currentPagePosition, false)
        indicator?.initIndicatorCount(realPagePosition)
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

    fun setTransformer(transformer: PageTransformer): Banner {
        compositePagetransformer.removeTransformer(defaultTransformer)
        compositePagetransformer.addTransformer(transformer)
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
        return this
    }

    fun setLivecyclerOwner(lifecycleOwner: LifecycleOwner): Banner {
        this.lifecycleOwner = lifecycleOwner
        lifecycleOwner.lifecycle.addObserver(this)
        return this
    }

    fun getCurrentPagePosition(): Int {
        val position = getRealPosition(currentPagePosition)
        return max(position, 0)
    }

    private fun startPolling() {
        if (isPollingStarted) {
            return
        }
        isPollingStarted = true
        postDelayed(autoPlayRunnable, turningNextPageDuration)
    }

    private fun stopPolling() {
        isPollingStarted = false
        removeCallbacks(autoPlayRunnable)
    }

    private fun isAutoPlay(): Boolean = autoPlay && realPagePosition > 1

    private fun resetPagerCount() {
        val externalAdapter = adapter.getExternalAdapter()
        if (externalAdapter == null || externalAdapter.itemCount == 0) {
            realPagePosition = 0
            draggingExtraPageCount = 0
        } else {
            realPagePosition = externalAdapter.itemCount
            draggingExtraPageCount = realPagePosition + 2 // + 2 保证第0页和最后一页 向左右滑动有数据
        }
    }

    /**
     * 轮播图第一个位置的数据为1,而不是0.
     * realPosition = 0
     */
    private fun getRealPosition(position: Int): Int {
        var realPosition = 0
        if (realPagePosition != 0) {
            realPosition = (position - 1) % realPagePosition
        }
        if (realPosition < 0) {
            realPosition += realPagePosition
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

        /**
         * 仅当首次或页面切换时会触发回调, 当页面切换后会先触发 onPageSelected 在触发 onPageScrolled
         */
        override fun onPageSelected(position: Int) {
            val onVirtualPage =
                currentPagePosition == 0 || currentPagePosition == draggingExtraPageCount || (position != currentPagePosition && draggingExtraPageCount - currentPagePosition == 1)
            currentPagePosition = position
            if (onVirtualPage) return
            val realPageSelectedPosition = getRealPosition(position)
            onPageChangeCallback?.onPageSelected(realPageSelectedPosition)
            indicator?.onPageSelected(realPageSelectedPosition)
        }

        /**
         * 当首次滚动时, 会调用多次该方法, 直到 positionOffset 的值由 0.x 到 1.0,
         * 当值等于 1.0 后会再次收到最后一个回调, 将值瞬间变为 0.0, onPageScrolled 结束
         * 此时会回调 onPageScrollStateChanged 将 state = 0(SCROLL_STATE_IDLE)
         */
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            val realPageSelectedPosition = getRealPosition(position)
            onPageChangeCallback?.onPageScrolled(realPageSelectedPosition, positionOffset, positionOffsetPixels)
            indicator?.onPageScrolled(realPageSelectedPosition, positionOffset, positionOffsetPixels)
        }

        override fun onPageScrollStateChanged(state: Int) {
            onPageChangeCallback?.onPageScrollStateChanged(state)
            indicator?.onPageScrollStateChanged(state)
            if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                if (currentPagePosition == 0) {
                    viewPager.setCurrentItem(realPagePosition, false)
                    currentPagePosition = 1
                } else if (currentPagePosition == draggingExtraPageCount - 1) {
                    viewPager.setCurrentItem(1, false)
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
            return if (realPagePosition > 1) draggingExtraPageCount else realPagePosition
        }

        override fun getItemViewType(position: Int): Int {
            return if (::externalAdapter.isInitialized) externalAdapter.getItemViewType(getRealPosition(position)) else -1
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val realPageSelectedPosition = getRealPosition(position)
            return externalAdapter.onBindViewHolder(holder, realPageSelectedPosition)
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

    private val adapterDataObserver = object : RecyclerView.AdapterDataObserver() {
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
            resetPagerCount()
            val startPosition = getCurrentPagePosition()
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

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        if (isAutoPlay()) {
            startPolling()
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        stopPolling()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        handler.removeCallbacksAndMessages(null)
    }

}
