package com.smarternote.feature.sport

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.DataBindingHolder
import com.smarternote.core.base.activity.BaseActivity
import com.smarternote.core.base.delegates.contentView
import com.smarternote.core.config.RouterPath
import com.smarternote.core.utils.logDebug
import com.smarternote.feature.sport.databinding.ActivityListBinding
import com.smarternote.feature.sport.databinding.ItemHorizontalBinding
import com.smarternote.feature.sport.databinding.ItemListBinding
import dagger.hilt.android.AndroidEntryPoint

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

        val adapter = ListAdapter().apply {
            animationEnable = true
            setItemAnimation(BaseQuickAdapter.AnimationType.AlphaIn)
        }

        binding.list.adapter = adapter
        adapter.submitList(DataCreator.titles)

    }

    class ListAdapter : BaseQuickAdapter<Title, ListAdapter.VH>(), HorizontalScrollObserver {

        private val horizontalScrollListener = HorizontalScrollListener()
        private val viewHolders = mutableSetOf<VH>()

        inner class VH(
            parent: ViewGroup,
            private onScrollListener: OnScrollListener,
            val binding: ItemListBinding = ItemListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ) : RecyclerView.ViewHolder(binding.root), HorizontalScrollObserver {

            private val horizontalAdapter = HorizontalAdapter()

            fun bind(item: List<String>?) {
                horizontalAdapter.submitList(item)
                binding.horizontalRecyclerView.adapter = horizontalAdapter
            }

            override fun onScroll(source: RecyclerView, dx: Int) {
                logDebug("ViewHolder onScroll")
                // 如果当前滑动的 RecyclerView 不是源 RecyclerView，则同步横向滑动
                if (binding.horizontalRecyclerView != source) {
                    viewHolders.forEach { viewHolder ->
                        val scroller = CustomLinearSmoothScroller(binding.horizontalRecyclerView.context)
                        scroller.targetPosition = binding.horizontalRecyclerView.computeHorizontalScrollOffset() + dx
                        viewHolder.binding.horizontalRecyclerView.layoutManager?.startSmoothScroll(scroller)
                    }
                }
            }
        }

        override fun onBindViewHolder(holder: VH, position: Int, item: Title?) {
            holder.bind(item?.childs)
            holder.binding.horizontalRecyclerView.addOnScrollListener(horizontalScrollListener)
            horizontalScrollListener.addObserver(holder)
            viewHolders.add(holder)
        }

        override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
            return VH(parent)
        }

        override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
            super.onDetachedFromRecyclerView(recyclerView)
            horizontalScrollListener.observers.clear()
            viewHolders.clear()
        }

        override fun onScroll(source: RecyclerView, dx: Int) {
            logDebug("Vertical onScroll")
            // 通知横向滑动
            horizontalScrollListener.onScrolled(source, dx, 0)
        }

        inner class HorizontalAdapter : BaseQuickAdapter<String, DataBindingHolder<ItemHorizontalBinding>>() {
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

class CustomLinearSmoothScroller(context: Context) : LinearSmoothScroller(context) {
    override fun getHorizontalSnapPreference(): Int {
        return SNAP_TO_START
    }
}