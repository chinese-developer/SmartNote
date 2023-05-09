package com.smarternote.core.utils

import android.widget.Toast
import com.smarternote.core.BaseApplication
import com.smarternote.themes.utils.runOnUiThread
import java.security.AccessController.getContext

var previousToastTampMillis = 0L
var previousToastMsg = ""

fun toast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
  runOnUiThread {
    if (previousToastMsg.isEmpty()) {
      Toast.makeText(BaseApplication.instance.applicationContext, msg, duration).show()
      previousToastMsg = msg
      previousToastTampMillis = System.currentTimeMillis()
    } else {
      val current = System.currentTimeMillis()
      if (current - previousToastTampMillis > 2000) {
        Toast.makeText(BaseApplication.instance.applicationContext, msg, duration).show()
        previousToastMsg = msg
        previousToastTampMillis = System.currentTimeMillis()
      } else {
        if (msg != previousToastMsg) {
          Toast.makeText(BaseApplication.instance.applicationContext, msg, duration).show()
          previousToastTampMillis = System.currentTimeMillis()
        }
      }
    }
  }
}