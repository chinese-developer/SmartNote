@file:Suppress("ObjectLiteralToLambda")

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

class ADBanner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private lateinit var bgaBanner: BGABanner
    private lateinit var magicIndicator: MagicIndicator
    private val circleNavigator by lazy { CircleNavigator(context) }
    private var adapter: BGABanner.Adapter<ImageView, String>? = null
    private var itemList = listOf<String>()

    init {
        if (!isInEditMode) {
            inflate(context, R.layout.core_layout_ad_banner, this)

            bgaBanner = findViewById(R.id.bga_banner)
            magicIndicator = findViewById(R.id.magic_indicator)

            circleNavigator.circleCount = itemList.size
            circleNavigator.circleColor = Color.RED
            circleNavigator.circleClickListener = CircleNavigator.OnCircleClickListener { index -> bgaBanner.currentItem = index }
            magicIndicator.navigator = circleNavigator

            bgaBanner.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    magicIndicator.onPageScrolled(position % itemList.size, positionOffset, positionOffsetPixels)
                }

                override fun onPageSelected(position: Int) {
                    magicIndicator.onPageSelected(position % itemList.size)
                }

                override fun onPageScrollStateChanged(state: Int) {
                    magicIndicator.onPageScrollStateChanged(state)
                }
            })
        }
    }

    fun setData(imageList: List<String>, loadImage: (ImageView, String) -> Unit) {
        this.itemList = imageList
        if (adapter == null) {
            adapter = BGABanner.Adapter<ImageView, String> { _, itemView, model, _ ->
                if (model != null) {
                    loadImage(itemView, model)
                }
            }
            bgaBanner.setAdapter(adapter)
            bgaBanner.setAutoPlayAble(true)
        }
        bgaBanner.setData(itemList, null)
    }

    fun setOnBannerItemClick(onBannerItemClick: (Int) -> Unit) {
        bgaBanner.setDelegate(object : BGABanner.Delegate<ImageView, String> {
            override fun onBannerItemClick(banner: BGABanner?, itemView: ImageView?, model: String?, position: Int) {
                onBannerItemClick(position)
            }
        })
    }

}
