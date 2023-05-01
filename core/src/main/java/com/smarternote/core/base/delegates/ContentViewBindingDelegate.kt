package com.smarternote.core.base.delegates

import android.app.Activity
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import kotlin.reflect.KProperty

class ContentViewBindingDelegate<in R : Activity, out T : ViewDataBinding>(
  @LayoutRes private val layoutRes: Int
) {

  private var binding: T? = null

  operator fun getValue(activity: R, property: KProperty<*>): T {
    if (binding == null) {
      binding = DataBindingUtil.setContentView(activity, layoutRes)
    }
    return binding!!
  }
}

fun <R : Activity, T : ViewDataBinding> contentView(
  @LayoutRes layoutRes: Int
): ContentViewBindingDelegate<R, T> = ContentViewBindingDelegate(layoutRes)
