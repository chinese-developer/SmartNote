package com.smarternote.core.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.smarternote.core.R
import com.smarternote.themes.view.MyImageView
import com.smarternote.themes.view.MyTextView

class MyToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val rootView = LayoutInflater.from(context).inflate(R.layout.core_layout_toolbar, this, true)
    private val tvTitle = rootView.findViewById<MyTextView>(R.id.tv_title)
    private val ivBack = rootView.findViewById<MyImageView>(R.id.iv_back)

    var title: String?
        get() = tvTitle.text.toString()
        set(value) {
            tvTitle.text = value
        }

    init {
        setBackgroundColor(ContextCompat.getColor(context, com.smarternote.themes.R.color.colorPrimary))
        elevation = context.dpF(10)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            widthMeasureSpec, MeasureSpec.makeMeasureSpec(context.dp(44), MeasureSpec.EXACTLY)
        )
    }

    fun setOnBackListener(listener: OnClickListener) {
        ivBack.setOnClickListener(listener)
    }

    private companion object {
        fun Context.dp(value: Int): Int {
            return (value * this.resources.displayMetrics.density + 0.5f).toInt()
        }

        fun Context.dpF(value: Int): Float {
            return value * this.resources.displayMetrics.density + 0.5f
        }
    }

}