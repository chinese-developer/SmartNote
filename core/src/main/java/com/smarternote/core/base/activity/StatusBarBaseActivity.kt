package com.smarternote.core.base.activity

import android.os.Bundle
import com.smarternote.core.utils.immersive
import com.smarternote.core.utils.immersiveDark

abstract class StatusBarBaseActivity : BaseActivity() {

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