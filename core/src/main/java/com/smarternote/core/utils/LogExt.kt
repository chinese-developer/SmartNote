package com.smarternote.core.utils

import com.smarternote.core.config.LogTagsConfig
import timber.log.Timber

fun logDebug(message: String) {
  val safeMessage = message.replace("null", "NULL")
  Timber.tag(LogTagsConfig.debug).d(safeMessage)
}

fun logTest(message: String) {
  val safeMessage = message.replace("null", "NULL")
  Timber.tag(LogTagsConfig.test).d(safeMessage)
}

fun logError(message: String) {
  val safeMessage = message.replace("null", "NULL")
  Timber.tag(LogTagsConfig.error).e(safeMessage)
}

fun logWarn(message: String) {
  val safeMessage = message.replace("null", "NULL")
  Timber.tag(LogTagsConfig.warn).w(safeMessage)
}

fun logVerbose(message: String) {
  val safeMessage = message.replace("null", "NULL")
  Timber.tag(LogTagsConfig.verbose).v(safeMessage)
}

fun logThread(message: String) {
  val safeMessage = message.replace("null", "NULL")
  Timber.tag(LogTagsConfig.debug).d("[%s] %s", Thread.currentThread().name, safeMessage)
}
