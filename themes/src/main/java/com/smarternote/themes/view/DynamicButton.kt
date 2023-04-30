package com.smarternote.themes.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleOwner
import com.smarternote.themes.R
import com.smarternote.themes.ThemeManager
import com.smarternote.themes.utils.copyToClipboard
import com.smarternote.themes.utils.openWebPage
import com.smarternote.themes.utils.showToast

class DynamicButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

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
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DynamicButton)
        val dynamicStyleResId = typedArray.getResourceId(R.styleable.DynamicButton_dynamic_style, 0)

        copyOnClick = typedArray.getBoolean(R.styleable.DynamicButton_dyncmic_copyOnClick, false)
        openUrlOnClick = typedArray.getBoolean(R.styleable.DynamicButton_dyncmic_openUrlOnClick, false)
        rippleOnClick = typedArray.getBoolean(R.styleable.DynamicButton_dyncmic_rippleOnClick, false)

        ThemeManager.dynamicViewsThemeConfig.observe(context as LifecycleOwner) { config ->
            setTextColor(ContextCompat.getColor(context, config.textColor))
        }

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
 * app:onClick="@{() -> viewModel.onButtonClick()}"
 */
@BindingAdapter("onClick")
fun setOnButtonClickListener(
    dynamicButton: DynamicButton,
    listener: (() -> Unit)?,
) {
    dynamicButton.onClickListener = listener
}