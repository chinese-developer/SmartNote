@file:Suppress("NotifyDataSetChanged")

package com.smarternote.feature.sport

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.DataBindingHolder
import com.smarternote.core.base.activity.BaseActivity
import com.smarternote.core.base.delegates.contentView
import com.smarternote.core.config.RouterPath
import com.smarternote.core.ui.banner.Banner
import com.smarternote.core.ui.recyclerview.layoutmanager.PreloadLinearLayoutManager
import com.smarternote.feature.sport.ViewPager2ScrollObserver.pageScrollStateFlow
import com.smarternote.feature.sport.databinding.ActivityVerticalRvWithVp2Binding
import com.smarternote.feature.sport.databinding.ItemHorizontalBinding
import com.smarternote.feature.sport.databinding.ItemVerticalRvWithVp2Binding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Route(path = RouterPath.Test.ViewPager2Page)
@AndroidEntryPoint
class VerticalRecyclerViewWithviewPager2Activity : BaseActivity() {
    private val binding by contentView<VerticalRecyclerViewWithviewPager2Activity, ActivityVerticalRvWithVp2Binding>(R.layout.activity_vertical_rv_with_vp2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this

        binding.toolbar.title = "VP2列表"
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
            val binding: ItemVerticalRvWithVp2Binding = ItemVerticalRvWithVp2Binding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ) : RecyclerView.ViewHolder(binding.root) {

            private val horizontalPagerAdapter = HorizontalPagerAdapter()

            val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    binding.viewPager2.apply {
                        val pageMargin = (layoutParams as? ViewGroup.MarginLayoutParams)?.let { it.leftMargin + it.rightMargin } ?: 0
                        val scrollToX: Int = (width + pageMargin) * position + positionOffsetPixels
                        val positionOffsetRatio = if (width == 0) 0f else positionOffsetPixels.toFloat() / width
                        lifecycleCoroutineScope.launch {
                            pageScrollStateFlow.emit(
                                ViewPager2ScrollObserver.OnPageChanged(
                                    viewPager = binding.viewPager2,
                                    onPageScrolled = ViewPager2ScrollObserver.OnPageChanged.OnPageScrolled(
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
                                ViewPager2ScrollObserver.OnPageChanged(
                                    viewPager = binding.viewPager2,
                                    onPageSelected = ViewPager2ScrollObserver.OnPageChanged.OnPageSelected(position)
                                )
                            )
                        }
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {
                    lifecycleCoroutineScope.launch {
                        pageScrollStateFlow.emit(
                            ViewPager2ScrollObserver.OnPageChanged(
                                viewPager = binding.viewPager2,
                                onPageScrollStateChanged = ViewPager2ScrollObserver.OnPageChanged.OnPageScrollStateChanged(state)
                            )
                        )
                    }
                }
            }

            init {
                binding.apply {
                    viewPager2.registerOnPageChangeCallback(pageChangeCallback)

                    this@VerticalRecyclerViewAdapter.lifecycleCoroutineScope.launch {
                        pageScrollStateFlow.collectLatest { data ->
                            if (data == null) return@collectLatest
                            val sourceviewPager = data.viewPager

                            data.onPageSelected?.let { source ->
                                if (viewPager2 != sourceviewPager) {
//                                    viewPager2.setCurrentItem(source.position, true)
                                }
                            }
                            data.onPageScrollStateChanged?.let { source ->

                            }
                            data.onPageScrolled?.let { source ->
                                val offset = source.positionOffset
                                val scrollToX = source.scrollToX

                                if (sourceviewPager != viewPager2
                                    && offset != 0f
                                    && viewPager2.scrollX != scrollToX
                                ) {
                                    viewPager2.scrollTo(scrollToX, 0)
                                }
                            }
                        }
                    }
                }
            }

            fun bind(item: List<String>?) {
                binding.viewPager2.offscreenPageLimit = item?.size ?: ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
                slowFlingRecyclerView(binding.viewPager2)
                binding.viewPager2.adapter = horizontalPagerAdapter
                horizontalPagerAdapter.submitList(item)
                binding.viewPager2.setCurrentItem(currentPage, false)
            }

            private fun slowFlingRecyclerView(viewPager2: ViewPager2) {
                try {
                    val recyclerView = viewPager2.getChildAt(0) as RecyclerView
                    // 禁用 RecyclerView 的过度滚动效果。
                    recyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                    val originalLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val proxyLayoutManager = PreloadLinearLayoutManager(viewPager2.context)
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
                } catch (e: NoSuchFieldException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
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
            holder.binding.viewPager2.unregisterOnPageChangeCallback(holder.pageChangeCallback)
        }

        inner class HorizontalPagerAdapter : BaseQuickAdapter<String, DataBindingHolder<ItemHorizontalBinding>>() {
            override fun onBindViewHolder(holder: DataBindingHolder<ItemHorizontalBinding>, position: Int, item: String?) {
                holder.binding.m = item
                holder.binding.executePendingBindings()
            }

            override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): DataBindingHolder<ItemHorizontalBinding> {
                return DataBindingHolder(R.layout.item_horizontal, parent)
            }
        }
    }
}