@file:Suppress("MemberVisibilityCanBePrivate")

package com.smarternote.core.base.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager.LayoutParams
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetDialogFragment : BottomSheetDialogFragment() {

  protected lateinit var dialog: BottomSheetDialog

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    dialog = BottomSheetDialog(requireContext(), com.smarternote.themes.R.style.Widget_Dialog_material3)
    dialog.window?.apply {
      decorView.setPadding(0, 0, 0, 0)
      val lp: LayoutParams = attributes
      lp.width = LayoutParams.MATCH_PARENT
      lp.height = LayoutParams.WRAP_CONTENT
      lp.windowAnimations = com.smarternote.themes.R.style.Animation_WindowEnterExit_BottomInOut
      lp.gravity = Gravity.BOTTOM
      attributes = lp
      setBackgroundDrawableResource(android.R.color.transparent)
    }

    dialog.setContentView(onCreateDialog())
    return dialog
  }

  abstract fun onCreateDialog(): View
}