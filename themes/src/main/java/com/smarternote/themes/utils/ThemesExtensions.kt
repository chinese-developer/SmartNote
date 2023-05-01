package com.smarternote.themes.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast

inline fun runOnUiThread(crossinline block: () -> Unit) {
  if (Looper.myLooper() == Looper.getMainLooper()) {
    block()
  } else {
    Handler(Looper.getMainLooper()).post { block() }
  }
}

/**
 * 扩展函数，实现将文本复制到剪贴板
 */
fun Context.copyToClipboard(text: CharSequence) {
  val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
  val clip = android.content.ClipData.newPlainText("Copied Text", text)
  clipboard.setPrimaryClip(clip)
}

/**
 * 扩展函数，实现弹出Toast
 */
fun Context.showToast(
  message: String,
  duration: Int = Toast.LENGTH_SHORT
) {
  runOnUiThread {
    Toast.makeText(this, message, duration).show()
  }
}

/**
 * 扩展函数，实现打开网页
 */
fun Context.openWebPage(url: String) {
  val webpage = Uri.parse(url)
  val intent = Intent(Intent.ACTION_VIEW, webpage)
  if (intent.resolveActivity(packageManager) != null) {
    startActivity(intent)
  }
}