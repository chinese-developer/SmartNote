package com.smarternote.themes.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.TextViewCompat
import androidx.databinding.BindingAdapter
import com.smarternote.themes.R
import com.smarternote.themes.utils.copyToClipboard
import com.smarternote.themes.utils.openWebPage
import com.smarternote.themes.utils.showToast

class MyButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

    private var copyOnClick = false
    private var openUrlOnClick = false
    private var rippleOnClick = false

    var onClickListener: (() -> Unit)? = null

    init {
        if (!isInEditMode) {
            applyStyle(context, attrs)

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
    }

    private fun applyStyle(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyButton)
        val myStyleResId = typedArray.getResourceId(R.styleable.MyButton_myButton_style, 0)

        copyOnClick = typedArray.getBoolean(R.styleable.MyButton_myButton_copyOnClick, false)
        openUrlOnClick = typedArray.getBoolean(R.styleable.MyButton_myButton_openUrlOnClick, false)
        rippleOnClick = typedArray.getBoolean(R.styleable.MyButton_myButton_rippleOnClick, false)

//        ThemeManager.dynamicViewsThemeConfig.observe(context as LifecycleOwner) { config ->
//            setTextColor(ContextCompat.getColor(context, config.textColor))
//        }

        if (myStyleResId != 0) {
            TextViewCompat.setTextAppearance(this, myStyleResId)
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
    view: MyButton,
    listener: (() -> Unit)?,
) {
    view.onClickListener = listener
}