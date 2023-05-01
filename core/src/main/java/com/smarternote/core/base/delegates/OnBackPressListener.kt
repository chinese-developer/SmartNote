package com.smarternote.core.base.delegates

/**
 * 返回键监听
 */
interface OnBackPressListener {
  /**
   * @return true 表示 Fragment 处理 back press event；false 则表示所有 fragment 都不处理交由Activity处理。
   */
  fun onBackPressed(): Boolean
}