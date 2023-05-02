@file:Suppress("ObjectLiteralToLambda")

package com.smarternote.core.ui.banner

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.ViewPager
import cn.bingoogolapple.bgabanner.BGABanner
import com.smarternote.core.R
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.buildins.circlenavigator.CircleNavigator

class Banner<T> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private lateinit var bgaBanner: BGABanner
    private lateinit var magicIndicator: MagicIndicator
    private val circleNavigator by lazy { CircleNavigator(context) }
    private var adapter: BGABanner.Adapter<ViewGroup, T>? = null
    private var itemList = listOf<T>()
    private var bindView: ((T) -> View)? = null

    init {
        if (!isInEditMode) {
            inflate(context, R.layout.core_layout_banner, this)

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

    fun setData(itemList: List<T>, bindView: (T) -> View) {
        this.itemList = itemList
        this.bindView = bindView
        if (adapter == null) {
            adapter = object : BGABanner.Adapter<ViewGroup, T> {
                override fun fillBannerItem(banner: BGABanner?, itemView: ViewGroup, model: T?, position: Int) {
                    if (model != null) {
                        val loadedView = bindView(model)
                        itemView.addView(loadedView)
                    }
                }
            }
            bgaBanner.setAdapter(adapter)
            bgaBanner.setAutoPlayAble(true)
        }
        bgaBanner.setData(itemList, null)
    }

    fun setOnBannerItemClick(onBannerItemClick: (Int) -> Unit) {
        bgaBanner.setDelegate(object : BGABanner.Delegate<ViewGroup, T> {
            override fun onBannerItemClick(banner: BGABanner?, itemView: ViewGroup?, model: T?, position: Int) {
                onBannerItemClick(position)
            }
        })
    }

}
