import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.widget.ViewPager2
import com.smarternote.core.R
import com.smarternote.core.ui.banner.BannerAdapter
import kotlin.math.abs

class Banner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val lifecycleOwner: LifecycleOwner? = null
) : FrameLayout(context, attrs, defStyleAttr), DefaultLifecycleObserver {

    private val viewPager: ViewPager2
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var bannerAdapter: BannerAdapter
    private var autoPlayRunnable = getAutoPlayRunnable()
    private var autoPlay = true

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.core_banner, this, true)
        viewPager = view.findViewById(R.id.viewPager)
//        indicator = view.findViewById(R.id.indicator)

        viewPager.setPageTransformer { page, position ->
            val absPos = abs(position)
            page.scaleY = (1 - absPos) * 0.15f + 0.85f
            page.alpha = 1 - absPos
            page.translationX = -page.width * position
            page.translationY = 30 * absPos
        }

        lifecycleOwner?.lifecycle?.addObserver(this)
    }

    private fun getAutoPlayRunnable() = object : Runnable {
        override fun run() {
            viewPager.currentItem = viewPager.currentItem + 1
            handler.postDelayed(this, 3000)
        }
    }

    fun setData(images: List<Int>, onItemClickListener: (Int) -> Unit) {
        bannerAdapter = BannerAdapter(images, onItemClickListener)
        viewPager.adapter = bannerAdapter
        viewPager.offscreenPageLimit = 1
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
    }

    fun onPageSelected(onPageSelected: (Int) -> Unit) {
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                onPageSelected(position % bannerAdapter.itemCount)
            }
        })
    }

    fun addPageTransformer(transformer: ViewPager2.PageTransformer) {
        viewPager.setPageTransformer(transformer)
    }

    override fun onStart(owner: LifecycleOwner) {
        if (autoPlay) {
            handler.postDelayed(autoPlayRunnable, 3000)
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        handler.removeCallbacks(autoPlayRunnable)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        handler.removeCallbacksAndMessages(null)
    }

    fun enableAutoPlay(enable: Boolean) {
        autoPlay = enable
        if (enable) {
            lifecycleOwner?.let { onStart(it) }
        } else {
            lifecycleOwner?.let { onStop(it) }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> lifecycleOwner?.let { onStop(it) }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> lifecycleOwner?.let { onStart(it) }
        }
        return super.dispatchTouchEvent(ev)
    }
}
