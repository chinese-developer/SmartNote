package com.smarternote.feature.sport

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.DataBindingHolder
import com.smarternote.core.base.activity.BaseActivity
import com.smarternote.core.base.activity.StatusBarBaseActivity
import com.smarternote.core.base.delegates.contentView
import com.smarternote.core.config.RouterPath
import com.smarternote.feature.sport.databinding.ActivityListBinding
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

        binding.list.adapter = ListAdapter(DataCreator.titles).apply {
            animationEnable = true
            setItemAnimation(BaseQuickAdapter.AnimationType.AlphaIn)
        }

    }

    class ListAdapter(items: List<Title> = emptyList()) : BaseQuickAdapter<Title, DataBindingHolder<ItemListBinding>>(items) {
        override fun onBindViewHolder(holder: DataBindingHolder<ItemListBinding>, position: Int, item: Title?) {

        }

        override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): DataBindingHolder<ItemListBinding> {
            return DataBindingHolder(R.layout.item_list, parent)
        }
    }
}