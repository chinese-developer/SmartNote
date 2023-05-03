package com.smarternote.feature.sport

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.DataBindingHolder
import com.smarternote.core.base.activity.StatusBarBaseActivity
import com.smarternote.core.ui.banner.Banner
import com.smarternote.core.ui.banner.IndicatorView
import com.smarternote.feature.sport.databinding.ItemBannerBinding

class SportActivity : StatusBarBaseActivity() {

    val imageUrl = "https://lh6.googleusercontent.com/-55osAWw3x0Q/URquUtcFr5I/AAAAAAAAAbs/rWlj1RUKrYI/s1024/A%252520Photographer.jpg"
    val imageUrlList = arrayListOf(imageUrl, imageUrl, imageUrl, imageUrl)

    private val indicator by lazy {
        IndicatorView(this)
            .setIndicatorRatio(4f)
            .setIndicatorRadius(2f)
            .setIndicatorSelectedRatio(6f)
            .setIndicatorSelectedRadius(2f)
            .setIndicatorSpacing(0f)
            .setIndicatorStyle(IndicatorView.IndicatorStyle.INDICATOR_CIRCLE_RECT)
            .setIndicatorColor(Color.parseColor("#1AFFFFFF"))
            .setIndicatorSelectorColor(Color.parseColor("#99FFFFFF"))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sport)

        val banner = findViewById<Banner>(R.id.banner)
        val adapter = BannerAdapter()

        banner.setAdapter(adapter)
            .setIndicator(indicator)
            .setAutoPlay(true)
            .build()

        adapter.submitList(imageUrlList)

    }

    class BannerAdapter : BaseQuickAdapter<String, DataBindingHolder<ItemBannerBinding>>() {
        override fun onBindViewHolder(holder: DataBindingHolder<ItemBannerBinding>, position: Int, item: String?) {
            holder.binding.imageUrl = item
            holder.binding.executePendingBindings()
        }

        override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): DataBindingHolder<ItemBannerBinding> {
            return DataBindingHolder(R.layout.item_banner, parent)
        }

    }
}