package com.smarternote.feature.sport

import android.os.Bundle
import android.widget.Toast
import com.smarternote.core.base.activity.StatusBarBaseActivity
import com.smarternote.core.ui.banner.Banner

class SportActivity : StatusBarBaseActivity() {

    val imageUrl = "https://lh6.googleusercontent.com/-55osAWw3x0Q/URquUtcFr5I/AAAAAAAAAbs/rWlj1RUKrYI/s1024/A%252520Photographer.jpg"
    val imageUrlList = arrayListOf(imageUrl, imageUrl, imageUrl, imageUrl)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sport)

        val banner = findViewById<Banner>(R.id.banner)
        banner.lifecycleOwner = this
        banner.setData(imageUrlList) { position ->
            Toast.makeText(this, "Clicked on item: $position", Toast.LENGTH_SHORT).show()
        }.setAutoPlay(true)

//        banner.addPageTransformer { page, position ->
//            page.alpha = 1 - Math.abs(position)
//            page.scaleY = 1 - Math.abs(position) * 0.1f
//        }
    }
}