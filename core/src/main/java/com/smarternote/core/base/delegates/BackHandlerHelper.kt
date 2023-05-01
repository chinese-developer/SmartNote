package com.smarternote.core.base.delegates

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

/**
 * <pre>
 * 1.Fragment需要自己处理BackPress事件，如果不处理，就交给子Fragment处理。都不处理则由Activity处理
 * 2.BackPress的传递由低层往深层传递，同一层级的中外层中的 Fragment优先处理。
 * 3.在Fragment中嵌套使用Fragment时，请使用getSupportChildFragmentManager
</pre> *
 */
object BackHandlerHelper {
  /**
   * 将back事件分发给 FragmentManager 中管理的子Fragment，如果该 FragmentManager 中的所有Fragment都
   * 没有处理back事件，则尝试 FragmentManager.popBackStack()
   *
   * @return 如果处理了back键则返回 **true**
   * @see .handleBackPress
   * @see .handleBackPress
   */
  fun handleBackPress(fragmentManager: FragmentManager): Boolean {
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
   * 将back事件分发给Fragment中的子Fragment,
   * 该方法调用了 [.handleBackPress]
   *
   * @return 如果处理了back键则返回 **true**
   */
  fun handleBackPress(fragment: Fragment): Boolean {
    return handleBackPress(fragment.childFragmentManager)
  }

  /**
   * 将back事件分发给Activity中的子Fragment,
   * 该方法调用了 [.handleBackPress]
   *
   * @return 如果处理了back键则返回 **true**
   */
  fun handleBackPress(fragmentActivity: FragmentActivity): Boolean {
    return handleBackPress(fragmentActivity.supportFragmentManager)
  }

  /**
   * 判断Fragment是否处理了Back键
   *
   * @return 如果处理了back键则返回 **true**
   */
  fun isFragmentBackHandled(fragment: Fragment?): Boolean {
    return (fragment != null && fragment.isVisible
      && fragment.userVisibleHint // getUserVisibleHint默认情况下为true，在ViewPager中会被使用到。
      && fragment is OnBackPressListener
      && (fragment as OnBackPressListener).onBackPressed())
  }
}