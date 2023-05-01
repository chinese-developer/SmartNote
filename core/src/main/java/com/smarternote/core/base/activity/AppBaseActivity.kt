package com.smarternote.core.base.activity

import android.os.Bundle
import com.architecture.runtime.extension.compat.immersive
import com.architecture.runtime.extension.compat.immersiveDark

abstract class AppBaseActivity : BaseActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    if (tintStatusBar()) {
      if (enableStatusBarLightMode()) {
        immersive()
      } else {
        immersiveDark()
      }
    }
  }

  protected open fun tintStatusBar() = true
  protected open fun enableStatusBarLightMode() = false

}