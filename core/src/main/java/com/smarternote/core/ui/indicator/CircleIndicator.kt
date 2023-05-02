package com.smarternote.core.ui.indicator

import android.content.Context
import android.database.DataSetObserver
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener

/**
 * CircleIndicator work with ViewPager
 */
class CircleIndicator constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BaseCircleIndicator(context, attrs, defStyleAttr) {

    private var mViewpager: ViewPager? = null

    fun setViewPager(viewPager: ViewPager?) {
        mViewpager = viewPager
        if (mViewpager != null && mViewpager!!.adapter != null) {
            mLastPosition = -1
            createIndicators()
            mViewpager!!.removeOnPageChangeListener(mInternalPageChangeListener)
            mViewpager!!.addOnPageChangeListener(mInternalPageChangeListener)
            mInternalPageChangeListener.onPageSelected(mViewpager!!.currentItem)
        }
    }

    private fun createIndicators() {
        val adapter = mViewpager!!.adapter
        val count = adapter?.count ?: 0
        createIndicators(count, mViewpager!!.currentItem)
    }

    private val mInternalPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageScrolled(
            position: Int, positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            if (mViewpager!!.adapter == null
                || mViewpager!!.adapter!!.count <= 0
            ) {
                return
            }
            animatePageSelected(position)
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }
    val dataSetObserver: DataSetObserver = object : DataSetObserver() {
        override fun onChanged() {
            super.onChanged()
            if (mViewpager == null) {
                return
            }
            val adapter = mViewpager!!.adapter
            val newCount = adapter?.count ?: 0
            val currentCount = childCount
            mLastPosition = if (newCount == currentCount) {
                // No change
                return
            } else if (mLastPosition < newCount) {
                mViewpager!!.currentItem
            } else {
                -1
            }
            createIndicators()
        }
    }

    @Deprecated("User ViewPager addOnPageChangeListener")
    fun setOnPageChangeListener(
        onPageChangeListener: OnPageChangeListener?
    ) {
        if (mViewpager == null) {
            throw NullPointerException("can not find Viewpager , setViewPager first")
        }
        mViewpager!!.removeOnPageChangeListener(onPageChangeListener!!)
        mViewpager!!.addOnPageChangeListener(onPageChangeListener)
    }
}