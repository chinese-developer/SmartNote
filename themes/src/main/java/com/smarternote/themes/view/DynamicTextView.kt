package com.smarternote.themes.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.TextViewCompat
import androidx.databinding.BindingAdapter
import com.smarternote.themes.R
import com.smarternote.themes.utils.copyToClipboard
import com.smarternote.themes.utils.openWebPage
import com.smarternote.themes.utils.showToast


class DynamicTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var copyOnClick = false
    private var openUrlOnClick = false
    private var rippleOnClick = false

    var onClickListener: (() -> Unit)? = null

    init {
        applyDynamicStyle(context, attrs)

        // 设置点击监听
        setOnClickListener {
            if (copyOnClick) {
                context.copyToClipboard(text)
                context.showToast("内容已复制！")
            }
            if (openUrlOnClick) {
                text?.let { urlString ->
                    if (urlString.startsWith("http") || urlString.startsWith("https")) {
                        context.openWebPage(urlString.toString())
                    }
                }
            }
            onClickListener?.invoke()
        }
    }

    private fun applyDynamicStyle(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DynamicTextView)
        val dynamicStyleResId = typedArray.getResourceId(R.styleable.DynamicEditText_dynamic_style, 0)
        copyOnClick = typedArray.getBoolean(R.styleable.DynamicTextView_dyncmic_copyOnClick, false)
        openUrlOnClick = typedArray.getBoolean(R.styleable.DynamicTextView_dyncmic_openUrlOnClick, false)
        rippleOnClick = typedArray.getBoolean(R.styleable.DynamicButton_dyncmic_rippleOnClick, false)

        if (dynamicStyleResId != 0) {
            TextViewCompat.setTextAppearance(this, dynamicStyleResId)
        }

        if (rippleOnClick) {
            setBackgroundResource(R.drawable.ripple_background)
        }


        typedArray.recycle()
    }
}

/**
 * app:onClick="@{() -> viewModel.onTextViewClick()}"
 */
@BindingAdapter("onClick")
fun setOnTextViewClickListener(
    dynamicTextView: DynamicTextView,
    listener: (() -> Unit)?,
) {
    dynamicTextView.onClickListener = listener
}