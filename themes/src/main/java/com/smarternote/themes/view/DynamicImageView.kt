@file:Suppress("unused")

package com.smarternote.themes.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.smarternote.themes.R

class DynamicImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    var imageUrl: String? = null
        set(value) {
            field = value
            loadImage()
        }

    var imageResId: Int? = null
        set(value) {
            field = value
            loadImage()
        }

    var placeholderResId: Int? = null
    var errorResId: Int? = null
    var errorDrawable: Drawable? = null
    var placeholderDrawable: Drawable? = null
    var transitionDuration: Int = 300
    var isGif: Boolean = false

    var onImageClickListener: (() -> Unit)? = null
    var onImageClickWithParamsListener: ((imageUrl: String?, imageResId: Int?) -> Unit)? = null
    private var lastClickTime: Long = 0

    init {
        applyDynamicStyle(context, attrs)

        setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime >= 500) {
                onImageClickListener?.invoke()
                onImageClickWithParamsListener?.invoke(imageUrl, imageResId)
                lastClickTime = currentTime
            }
        }
    }

    private fun applyDynamicStyle(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DynamicImageView)
        val dynamicStyleResId = typedArray.getResourceId(R.styleable.DynamicImageView_dynamic_style, 0)

        typedArray.recycle()
    }

    private fun loadImage() {
        val requestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .onlyRetrieveFromCache(isGif)

        errorDrawable?.let { requestOptions.error(it) }
        placeholderDrawable?.let { requestOptions.placeholder(it) }
        placeholderResId?.let { requestOptions.placeholder(it) }
        errorResId?.let { requestOptions.error(it) }

        Glide.with(context)
            .setDefaultRequestOptions(requestOptions)
            .load(imageUrl ?: imageResId)
            .transition(DrawableTransitionOptions.withCrossFade(transitionDuration))
            .into(this)
    }
}


@BindingAdapter(
    "imageUrl", "imageResId", "placeholderResId", "errorResId",
    "transitionDuration", "errorDrawable", "placeholderDrawable",
    requireAll = false
)
fun setImageProperties(
    dynamicImageView: DynamicImageView,
    imageUrl: String?,
    imageResId: Int?,
    placeholderResId: Int?,
    errorResId: Int?,
    errorDrawable: Drawable?,
    placeholderDrawable: Drawable?,
    transitionDuration: Int?,
    isGif: Boolean?,
) {
    dynamicImageView.imageUrl = imageUrl
    dynamicImageView.imageResId = imageResId
    dynamicImageView.placeholderResId = placeholderResId
    dynamicImageView.errorResId = errorResId
    dynamicImageView.placeholderDrawable = placeholderDrawable
    dynamicImageView.errorDrawable = errorDrawable
    dynamicImageView.transitionDuration = transitionDuration ?: 300
    dynamicImageView.isGif = isGif ?: false
}

/**
 * app:onClick="@{() -> viewModel.onImageClick()}"
 * app:onClickWithParams = "@{(imageUrl, imageResId) -> viewModel.onImageClick(imageUrl, imageResId)}"
 */
@BindingAdapter("onClick, onClickWithParams", requireAll = false)
fun setOnImageClickListener(
    dynamicImageView: DynamicImageView,
    listener: (() -> Unit)?,
    listenerWithParams: ((imageUrl: String?, imageResId: Int?) -> Unit)?
) {
    dynamicImageView.onImageClickListener = listener
    dynamicImageView.onImageClickWithParamsListener = listenerWithParams
}
