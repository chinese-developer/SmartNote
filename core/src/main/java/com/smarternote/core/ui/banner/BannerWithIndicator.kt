package com.smarternote.core.ui.banner

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.ViewPager
import cn.bingoogolapple.bgabanner.BGABanner
import com.smarternote.core.R
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.buildins.circlenavigator.CircleNavigator

class BannerWithIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    
    private lateinit var banner: BGABanner
    private lateinit var magicIndicator: MagicIndicator

    init {
        if (!isInEditMode) {
            inflate(context, R.layout.core_layout_banner_with_indicator, this)

            banner = findViewById(R.id.banner)
            magicIndicator = findViewById(R.id.indicator)
        }
    }

    fun <T>setupBannerAndIndicator(imageList: List<T>, loadImage: (ImageView, T?) -> Unit, onBannerItemClick: (Int) -> Unit) {
        banner.setAdapter(BGABanner.Adapter<ImageView, T> { _, itemView, model, _ ->
            loadImage(itemView, model)
        })

        banner.setData(imageList, null)
        banner.setAutoPlayAble(true)
        banner.setDelegate { banner, itemView, model, position -> onBannerItemClick(position) }

        val circleNavigator = CircleNavigator(context)
        circleNavigator.circleCount = imageList.size
        circleNavigator.circleColor = Color.RED
        circleNavigator.circleClickListener = CircleNavigator.OnCircleClickListener { index -> banner.setCurrentItem(index) }
        magicIndicator.navigator = circleNavigator

        banner.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                magicIndicator.onPageScrolled(position % imageList.size, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                magicIndicator.onPageSelected(position % imageList.size)
            }

            override fun onPageScrollStateChanged(state: Int) {
                magicIndicator.onPageScrollStateChanged(state)
            }
        })
    }

}
