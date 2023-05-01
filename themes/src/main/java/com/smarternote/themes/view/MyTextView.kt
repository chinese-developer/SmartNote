package com.smarternote.themes.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.TextViewCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.smarternote.themes.R
import com.smarternote.themes.utils.copyToClipboard
import com.smarternote.themes.utils.openWebPage
import com.smarternote.themes.utils.showToast


class MyTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr), LifecycleObserver {

    private var copyOnClick = false
    private var openUrlOnClick = false
    private var rippleOnClick = false

    var onClickListener: (() -> Unit)? = null

    init {
        applyStyle(context, attrs)

        if (context is LifecycleOwner) {
            context.lifecycle.addObserver(this)
        }

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

    private fun applyStyle(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyTextView)
        val myStyleResId = typedArray.getResourceId(R.styleable.MyTextView_myTextView_style, 0)

        copyOnClick = typedArray.getBoolean(R.styleable.MyTextView_myTextView_copyOnClick, false)
        openUrlOnClick = typedArray.getBoolean(R.styleable.MyTextView_myTextView_openUrlOnClick, false)
        rippleOnClick = typedArray.getBoolean(R.styleable.MyTextView_myTextView_rippleOnClick, false)

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
 * app:onClick="@{() -> viewModel.onTextViewClick()}"
 */
@BindingAdapter("onClick")
fun setOnTextViewClickListener(
    view: MyTextView,
    listener: (() -> Unit)?,
) {
    view.onClickListener = listener
}