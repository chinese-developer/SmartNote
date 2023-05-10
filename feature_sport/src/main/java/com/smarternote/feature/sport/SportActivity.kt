package com.smarternote.feature.sport

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.DataBindingHolder
import com.smarternote.core.base.activity.StatusBarBaseActivity
import com.smarternote.core.config.RouterPath
import com.smarternote.core.ui.banner.Banner
import com.smarternote.core.utils.logDebug
import com.smarternote.core.utils.toast
import com.smarternote.feature.sport.SportActivity.BannerAdapter2.VH
import com.smarternote.feature.sport.databinding.ItemBannerBinding

class SportActivity : StatusBarBaseActivity() {

    val imageUrl = "https://lh6.googleusercontent.com/-55osAWw3x0Q/URquUtcFr5I/AAAAAAAAAbs/rWlj1RUKrYI/s1024/A%252520Photographer.jpg"
    val imageUrl2 = "https://images.techhive.com/images/article/2017/01/google-android-apps-100705848-large.jpg?auto=webp&quality=85,70"
    val imageUrlList = arrayListOf(imageUrl, imageUrl2, imageUrl, imageUrl2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sport)

        val banner = findViewById<Banner>(R.id.banner)
        val adapter = BannerAdapter()
        // val adapter = BannerAdapter2()

        banner.setAdapter(adapter)
            .setAutoPlay(true)
            .setLivecyclerOwner(this)
            .build()

        lifecycleScope.launchWhenResumed {
            adapter.submitList(imageUrlList)
        }

        adapter.animationEnable = true
    }

    class BannerAdapter : BaseQuickAdapter<String, DataBindingHolder<ItemBannerBinding>>() {
        override fun onBindViewHolder(holder: DataBindingHolder<ItemBannerBinding>, position: Int, item: String?) {
            holder.binding.imageUrl = item
            holder.binding.imageView.setOnClickListener {
                toast("position$position")
                ARouter.getInstance()
                    .build(RouterPath.Test.Matches)
                    .navigation()
            }
            holder.binding.executePendingBindings()
        }

        override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): DataBindingHolder<ItemBannerBinding> {
            return DataBindingHolder(R.layout.item_banner, parent)
        }

    }

    class BannerAdapter2(
        private val items: MutableList<String> = mutableListOf()
    ) : RecyclerView.Adapter<VH>() {

        class VH(
            parent: ViewGroup,
            val binding: ItemBannerBinding = ItemBannerBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        ) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(parent)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = items[position]
            logDebug("更新了数据position=$position")
            holder.binding.imageUrl = item
            holder.binding.executePendingBindings()
        }

        override fun getItemCount(): Int = items.size

        @SuppressLint("NotifyDataSetChanged")
        fun submitList(data: List<String>?) {
            items.clear()
            if (!data.isNullOrEmpty()) {
                items.addAll(data)
            }
            notifyDataSetChanged()
        }
    }
}