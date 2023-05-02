package com.smarternote.feature.sport

import android.os.Bundle
import com.smarternote.core.base.activity.StatusBarBaseActivity
import com.smarternote.core.ui.banner.BannerWithIndicator

class SportActivity : StatusBarBaseActivity() {

    val imageUrl = "https://lh6.googleusercontent.com/-55osAWw3x0Q/URquUtcFr5I/AAAAAAAAAbs/rWlj1RUKrYI/s1024/A%252520Photographer.jpg"
    val imageUrlList = arrayListOf(imageUrl, imageUrl, imageUrl, imageUrl)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sport)

        val banner = findViewById<BannerWithIndicator>(R.id.banner)
        banner.setupBannerAndIndicator(imageUrlList, { imageView, s ->  }, { i ->  })

    }
}