package com.smarternote.feature.sport

import android.os.Bundle
import android.view.LayoutInflater
import com.smarternote.core.base.activity.StatusBarBaseActivity
import com.smarternote.core.ui.banner.Banner
import com.smarternote.feature.sport.databinding.ItemBannerBinding

class SportActivity : StatusBarBaseActivity() {

    val imageUrl = "https://lh6.googleusercontent.com/-55osAWw3x0Q/URquUtcFr5I/AAAAAAAAAbs/rWlj1RUKrYI/s1024/A%252520Photographer.jpg"
    val imageUrlList = arrayListOf(imageUrl, imageUrl, imageUrl, imageUrl)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sport)

        val banner = findViewById<Banner<String>>(R.id.banner)
        banner.setData(imageUrlList) { imageUrl ->
            val binding = ItemBannerBinding.inflate(LayoutInflater.from(this), null, false)
            binding.imageUrl = imageUrl
            binding.executePendingBindings()
            binding.root
        }
    }
}