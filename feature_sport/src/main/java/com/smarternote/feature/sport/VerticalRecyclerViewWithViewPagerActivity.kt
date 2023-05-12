@file:Suppress("NotifyDataSetChanged")

package com.smarternote.feature.sport

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.smarternote.core.base.activity.BaseActivity
import com.smarternote.core.base.delegates.contentView
import com.smarternote.core.config.RouterPath
import com.smarternote.core.utils.logDebug
import com.smarternote.feature.sport.ViewPagerScrollObserver.pageScrollStateFlow
import com.smarternote.feature.sport.databinding.ActivityVerticalRvWithVp1Binding
import com.smarternote.feature.sport.databinding.ItemHorizontalBinding
import com.smarternote.feature.sport.databinding.ItemVerticalRvWithVp1Binding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Route(path = RouterPath.Test.ViewPager1Page)
@AndroidEntryPoint
class VerticalRecyclerViewWithViewPagerActivity : BaseActivity() {

    private val binding by contentView<VerticalRecyclerViewWithViewPagerActivity, ActivityVerticalRvWithVp1Binding>(R.layout.activity_vertical_rv_with_vp1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this

        binding.toolbar.title = "VP1列表"
        binding.toolbar.setOnBackListener {
            finish()
        }

        val adapter = VerticalRecyclerViewAdapter(lifecycleScope).apply {
            animationEnable = true
            setItemAnimation(BaseQuickAdapter.AnimationType.AlphaIn)
        }

        binding.list.adapter = adapter
        adapter.submitList(DataCreator.titles)

    }

    class VerticalRecyclerViewAdapter(
        private val lifecycleCoroutineScope: LifecycleCoroutineScope
    ) : BaseQuickAdapter<Title, VerticalRecyclerViewAdapter.VH>() {

        private var currentPage = 0

        inner class VH(
            parent: ViewGroup,
            val binding: ItemVerticalRvWithVp1Binding = ItemVerticalRvWithVp1Binding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ) : RecyclerView.ViewHolder(binding.root) {

            var pageScrollJob: Job? = null
            private val itemViews = mutableListOf<View>()
            private val horizontalPagerAdapter = HorizontalPagerAdapter(itemViews)

            val pageChangeCallback = object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    binding.viewPager.apply {
                        val pageMargin = (layoutParams as? ViewGroup.MarginLayoutParams)?.let { it.leftMargin + it.rightMargin } ?: 0
                        val scrollToX: Int = (width + pageMargin) * position + positionOffsetPixels
                        val positionOffsetRatio = if (width == 0) 0f else positionOffsetPixels.toFloat() / width
                        lifecycleCoroutineScope.launch {
                            pageScrollStateFlow.emit(
                                ViewPagerScrollObserver.OnPageChanged(
                                    viewPager = binding.viewPager,
                                    onPageScrolled = ViewPagerScrollObserver.OnPageChanged.OnPageScrolled(
                                        position = position,
                                        positionOffset = positionOffset,
                                        positionOffsetPixels = positionOffsetPixels,
                                        scrollToX = scrollToX,
                                        positionOffsetRatio = positionOffsetRatio
                                    )
                                )
                            )
                        }
                    }
                }

                override fun onPageSelected(position: Int) {
                    lifecycleCoroutineScope.launch {
                        currentPage = position
                        lifecycleCoroutineScope.launch {
                            pageScrollStateFlow.emit(
                                ViewPagerScrollObserver.OnPageChanged(
                                    viewPager = binding.viewPager,
                                    onPageSelected = ViewPagerScrollObserver.OnPageChanged.OnPageSelected(position)
                                )
                            )
                        }
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {
                    lifecycleCoroutineScope.launch {
                        pageScrollStateFlow.emit(
                            ViewPagerScrollObserver.OnPageChanged(
                                viewPager = binding.viewPager,
                                onPageScrollStateChanged = ViewPagerScrollObserver.OnPageChanged.OnPageScrollStateChanged(state)
                            )
                        )
                    }
                }
            }

            fun viewAttachedToWindow() {
                binding.apply {
                    viewPager.addOnPageChangeListener(pageChangeCallback)

                    pageScrollJob = this@VerticalRecyclerViewAdapter.lifecycleCoroutineScope.launch {
                        pageScrollStateFlow.collectLatest { data ->
                            if (data == null) return@collectLatest
                            val sourceViewPager = data.viewPager

                            data.onPageSelected?.let { source ->
                                logDebug("onPageSelected[${pageChangeCallback.hashCode()}]")
                                if (viewPager != sourceViewPager) {
//                                    viewPager.setCurrentItem(source.position, true)
                                }
                            }
                            data.onPageScrollStateChanged?.let { source ->

                            }
                            data.onPageScrolled?.let { source ->
                                val offset = source.positionOffset
                                val scrollToX = source.scrollToX

                                if (sourceViewPager != viewPager
                                    && offset != 0f
                                    && viewPager.scrollX != scrollToX
                                ) {
                                    viewPager.scrollTo(scrollToX, 0)
                                }
                            }
                        }
                    }
                }
            }

            fun bind(item: List<String>?) {
                item?.forEach {
                    val itemBinding = DataBindingUtil.inflate<ItemHorizontalBinding>(
                        LayoutInflater.from(context),
                        R.layout.item_horizontal,
                        null,
                        false
                    )
                    itemViews.add(itemBinding.root)
                    itemBinding.m = it
                }

                binding.viewPager.adapter = horizontalPagerAdapter
//                binding.viewPager.setCurrentItem(currentPage, false)
            }
        }

        override fun onBindViewHolder(holder: VH, position: Int, item: Title?) {
            holder.bind(item?.childs)
        }

        override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
            return VH(parent)
        }

        override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
            super.onViewRecycled(holder)
            holder as VH
            holder.binding.viewPager.removeOnPageChangeListener(holder.pageChangeCallback)
            holder.pageScrollJob?.cancel()
            holder.pageScrollJob = null
        }

        override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
            super.onViewAttachedToWindow(holder)
            holder as VH
            holder.viewAttachedToWindow()
        }

        override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
            super.onViewDetachedFromWindow(holder)
            holder as VH
            holder.binding.viewPager.removeOnPageChangeListener(holder.pageChangeCallback)
            holder.pageScrollJob?.cancel()
            holder.pageScrollJob = null
        }

        inner class HorizontalPagerAdapter(private val data: List<View>): PagerAdapter() {
            override fun getCount(): Int = data.size

            override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val item = data[position]
                container.addView(item)
                return item
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                container.removeView(`object` as View)
            }

            override fun getItemPosition(`object`: Any): Int {
                return POSITION_NONE
            }

        }
    }
}