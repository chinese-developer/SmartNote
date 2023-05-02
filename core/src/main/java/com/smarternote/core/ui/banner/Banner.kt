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
    private var adapter: BGABanner.Adapter<View, T>? = null
    private var viewList = listOf<View>()
    private var bindView: ((View, T) -> Unit)? = null

    init {
        if (!isInEditMode) {
            inflate(context, R.layout.core_layout_banner, this)

            bgaBanner = findViewById(R.id.bga_banner)
            magicIndicator = findViewById(R.id.magic_indicator)

            circleNavigator.circleCount = viewList.size
            circleNavigator.circleColor = Color.RED
            circleNavigator.circleClickListener = CircleNavigator.OnCircleClickListener { index -> bgaBanner.currentItem = index }
            magicIndicator.navigator = circleNavigator

            bgaBanner.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    magicIndicator.onPageScrolled(position % viewList.size, positionOffset, positionOffsetPixels)
                }

                override fun onPageSelected(position: Int) {
                    magicIndicator.onPageSelected(position % viewList.size)
                }

                override fun onPageScrollStateChanged(state: Int) {
                    magicIndicator.onPageScrollStateChanged(state)
                }
            })
        }
    }

    fun setData(viewList: List<View>, bindView: (View, T) -> Unit) {
        this.viewList = viewList
        this.bindView = bindView
        if (adapter == null) {
            adapter = object : BGABanner.Adapter<View, T> {
                override fun fillBannerItem(banner: BGABanner?, itemView: View, model: T?, position: Int) {
                    if (model != null) {
                        bindView(itemView, model)
                    }
                }
            }
            bgaBanner.setAdapter(adapter)
            bgaBanner.setAutoPlayAble(true)
        }
        bgaBanner.setData(this.viewList, null)
    }


    fun setOnBannerItemClick(onBannerItemClick: (Int) -> Unit) {
        bgaBanner.setDelegate(object : BGABanner.Delegate<ViewGroup, T> {
            override fun onBannerItemClick(banner: BGABanner?, itemView: ViewGroup?, model: T?, position: Int) {
                onBannerItemClick(position)
            }
        })
    }

}
