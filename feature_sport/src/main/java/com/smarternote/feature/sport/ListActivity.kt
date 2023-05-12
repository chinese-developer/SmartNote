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
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.DataBindingHolder
import com.smarternote.core.base.activity.BaseActivity
import com.smarternote.core.base.delegates.contentView
import com.smarternote.core.config.RouterPath
import com.smarternote.feature.sport.databinding.ActivityListBinding
import com.smarternote.feature.sport.databinding.ItemHorizontalBinding
import com.smarternote.feature.sport.databinding.ItemListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Route(path = RouterPath.Test.Matches)
@AndroidEntryPoint
class ListActivity : BaseActivity() {

    private val binding by contentView<ListActivity, ActivityListBinding>(R.layout.activity_list)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this

        binding.toolbar.title = "列表"
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
            val binding: ItemListBinding = ItemListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ) : RecyclerView.ViewHolder(binding.root) {

            private val itemViews = mutableListOf<View>()
            private val horizontalPagerAdapter = HorizontalPagerAdapter2(itemViews)

            val pageChangeCallback = object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    binding.viewPager2.apply {
                        val pageMargin = (layoutParams as? ViewGroup.MarginLayoutParams)?.let { it.leftMargin + it.rightMargin } ?: 0
                        val scrollToX: Int = (width + pageMargin) * position + positionOffsetPixels
                        val positionOffsetRatio = if (width == 0) 0f else positionOffsetPixels.toFloat() / width
                        lifecycleCoroutineScope.launch {
                            pageScrollStateFlow.emit(
                                OnPageChanged(
                                    viewPager2 = binding.viewPager2,
                                    onPageScrolled = OnPageChanged.OnPageScrolled(
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
                                OnPageChanged(
                                    viewPager2 = binding.viewPager2,
                                    onPageSelected = OnPageChanged.OnPageSelected(position)
                                )
                            )
                        }
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {
                    lifecycleCoroutineScope.launch {
                        pageScrollStateFlow.emit(
                            OnPageChanged(
                                viewPager2 = binding.viewPager2,
                                onPageScrollStateChanged = OnPageChanged.OnPageScrollStateChanged(state)
                            )
                        )
                    }
                }
            }

            init {
                binding.apply {
                    viewPager2.addOnPageChangeListener(pageChangeCallback)

                    this@VerticalRecyclerViewAdapter.lifecycleCoroutineScope.launch {
                        pageScrollStateFlow.collectLatest { data ->
                            if (data == null) return@collectLatest
                            val sourceViewPager = data.viewPager2

                            data.onPageSelected?.let { source ->
                                if (viewPager2 != sourceViewPager) {
//                                    viewPager2.setCurrentItem(source.position, true)
                                }
                            }
                            data.onPageScrollStateChanged?.let { source ->

                            }
                            data.onPageScrolled?.let { source ->
                                val offset = source.positionOffset
                                val scrollToX = source.scrollToX

                                if (sourceViewPager != viewPager2
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
//                binding.viewPager2.offscreenPageLimit = item?.size ?: ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
//                binding.viewPager2.adapter = horizontalPagerAdapter
//                horizontalPagerAdapter.submitList(item)
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

                binding.viewPager2.adapter = horizontalPagerAdapter
                binding.viewPager2.setCurrentItem(currentPage, false)
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
            holder.binding.viewPager2.removeOnPageChangeListener(holder.pageChangeCallback)
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

        inner class HorizontalPagerAdapter2(private val data: List<View>): PagerAdapter() {
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