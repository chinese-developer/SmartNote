package com.smarternote.themes.view

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.TextViewCompat
import androidx.core.widget.addTextChangedListener
import com.smarternote.themes.R

class MyEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val editText: AppCompatEditText
    private val deleteIcon: ImageView
    private var onDeleteIconClickListener: (() -> Unit)? = null
    private var deleteIconWidth: Int = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.dynamic_edit_text, this, true)
        editText = findViewById(R.id.edit_text)
        deleteIcon = findViewById(R.id.delete_icon)
        deleteIconWidth = deleteIcon.layoutParams.width

        applyStyle(context, attrs)

        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (editText.text?.isNotEmpty() == true) {
                    animateDeleteIcon(show = true)
                }
            } else {
                animateDeleteIcon(show = false)
            }
        }

        editText.addTextChangedListener {
            if (it?.isNotEmpty() == true) {
                if (editText.hasFocus()) {
                    animateDeleteIcon(show = true)
                }
            } else {
                animateDeleteIcon(show = false)
            }
        }

        deleteIcon.setOnClickListener {
            editText.setText("")
            onDeleteIconClickListener?.invoke()
        }
    }

    private fun applyStyle(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyEditText)
        val myStyleResId = typedArray.getResourceId(R.styleable.MyEditText_myEditText_style, 0)

        if (myStyleResId != 0) {
            TextViewCompat.setTextAppearance(editText, myStyleResId)
        }

        typedArray.recycle()
    }

    private fun animateDeleteIcon(show: Boolean) {
        val start = if (show) 0 else deleteIconWidth
        val end = if (show) deleteIconWidth else 0
        val animator = ValueAnimator.ofInt(start, end)
        animator.addUpdateListener { valueAnimator ->
            val layoutParams = deleteIcon.layoutParams
            layoutParams.width = valueAnimator.animatedValue as Int
            deleteIcon.layoutParams = layoutParams
        }
        animator.duration = 300
        animator.start()
    }

    fun setOnDeleteIconClickListener(listener: (() -> Unit)?) {
        onDeleteIconClickListener = listener
    }
}
