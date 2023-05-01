package com.smarternote.core.base.delegates

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

/**
 * 1.Fragment需要自己处理BackPress事件，如果不处理，就交给子Fragment处理。都不处理则由Activity处理
 * 2.BackPress的传递由低层往深层传递，同一层级的中外层中的 Fragment优先处理。
 * 3.在Fragment中嵌套使用Fragment时，请使用getSupportChildFragmentManager
 */
object BackHandlerHelper {
  /**
   * 将返回事件分发给 FragmentManager 中管理的子 Fragment，如果该 FragmentManager 中的所有 Fragment 都没有处理back事件，则尝试 FragmentManager.popBackStack()
   *
   * @return 如果处理了back键则返回 true
   * @see .handleBackPress
   * @see .handleBackPress
   */
  private fun handleBackPress(fragmentManager: FragmentManager): Boolean {
    val fragments = fragmentManager.fragments
    for (i in fragments.indices.reversed()) {
      val child = fragments[i]
      if (isFragmentBackHandled(child)) {
        return true
      }
    }
    return false
  }

  /**
   * 将返回事件分发给 Fragment 中的子 Fragment, 该方法调用了 [.handleBackPress]
   *
   * @return 如果处理了返回键则返回 true
   */
  fun handleBackPress(fragment: Fragment): Boolean {
    return handleBackPress(fragment.childFragmentManager)
  }

  /**
   * 将返回事件分发给 Activity 中的子 Fragment, 该方法调用了 [.handleBackPress]
   *
   * @return 如果处理了返回键则返回 true
   */
  fun handleBackPress(fragmentActivity: FragmentActivity): Boolean {
    return handleBackPress(fragmentActivity.supportFragmentManager)
  }

  /**
   * 判断 Fragment 是否处理了返回键
   *
   * @return 如果处理了返回键则返回 true
   */
  private fun isFragmentBackHandled(fragment: Fragment?): Boolean {
    @Suppress("DEPRECATION")
    return (fragment != null && fragment.isVisible
      && fragment.userVisibleHint // userVisibleHint 默认情况下为 true，在 ViewPager 中会被使用到。
      && fragment is OnBackPressListener
      && (fragment as OnBackPressListener).onBackPressed())
  }
}