package com.smarternote.feature.sport

import androidx.viewpager.widget.ViewPager
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow


object ViewPagerScrollObserver {

    val pageScrollSharedFlow = MutableSharedFlow<OnPageChanged>(
        /**
         * 重放数据个数，当新订阅者注册时会重放缓存 replay 个数
         */
        replay = 0,
        /**
         * 额外缓存容量，在 replay 之外的额外容量，ShareFlow 的缓存容量
         */
        extraBufferCapacity = Int.MAX_VALUE,
        /**
         * 缓存溢出策略，即缓存容量 capacity 满时的处理策略
         */
        onBufferOverflow = BufferOverflow.SUSPEND
    )

    val pageScrollStateFlow = MutableStateFlow<OnPageChanged?>(null)

    data class OnPageChanged(
        /**
         * 当前绑定的 ViewPager 对象
         */
        val viewPager: ViewPager,
        val onPageScrolled: OnPageScrolled? = null,
        val onPageSelected: OnPageSelected? = null,
        val onPageScrollStateChanged: OnPageScrollStateChanged? = null,
    ) {

        data class OnPageScrolled(
            /**
             * 当前页面 position
             */
            val position: Int,
            /**
             * 偏移量(0.x-1.0)
             */
            val positionOffset: Float,
            /**
             * 偏移量 Int(0,1)
             */
            val positionOffsetPixels: Int,
            /**
             * 横向滑动发生的偏移量 scrollToX
             */
            val scrollToX: Int,
            /**
             * ViewPager2 滑动比例
             */
            val positionOffsetRatio: Float
        )

        data class OnPageSelected(
            /**
             * 当前页面 position
             */
            val position: Int
        )

        data class OnPageScrollStateChanged(
            val state: Int
        )
    }
}