@file:Suppress("NotifyDataSetChanged")
package com.smarternote.feature.sport

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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

    class ListAdapter : BaseQuickAdapter<Title, ListAdapter.VH>(), SyncScrollListener {

        inner class VH(
            parent: ViewGroup,
            val binding: ItemListBinding = ItemListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ) : RecyclerView.ViewHolder(binding.root) {

            private val onScroll: (dx: Int) -> Unit = { dx ->
                if (binding.horizontalRecyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                    binding.horizontalRecyclerView.scrollBy(dx, 0)
                }
            }

            private val syncScrollListener = SyncScrollListenerImpl(this@ListAdapter, onScroll)

            private val horizontalAdapter = HorizontalAdapter()

            fun bind(item: List<String>?) {
                horizontalAdapter.submitList(item)
//                binding.horizontalRecyclerView.layoutManager = SyncScrollLinearLayoutManager(binding.horizontalRecyclerView.context)
                binding.horizontalRecyclerView.adapter = horizontalAdapter
                binding.horizontalRecyclerView.addOnScrollListener(syncScrollListener)
            }

        }

        override fun onBindViewHolder(holder: VH, position: Int, item: Title?) {
            holder.bind(item?.childs)
        }

        override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
            return VH(parent)
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

        override fun onScrolled(dx: Int) {
            notifyDataSetChanged()
        }
    }
}