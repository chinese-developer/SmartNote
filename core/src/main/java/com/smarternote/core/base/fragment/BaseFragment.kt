package com.architecture.runtime.base.fragment

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.architecture.runtime.base.activity.BackHandlerHelper
import com.architecture.runtime.base.activity.OnBackPressListener
import com.architecture.runtime.widget.loading.LoadingDialog
import com.architecture.runtime.widget.loading.LoadingHandler
import com.gsmc.sport.sdk.core.common.TagsFactory
import me.jessyan.autosize.internal.CustomAdapt
import timber.log.Timber

open class BaseFragment : Fragment(), OnBackPressListener, CustomAdapt {

  /** 返回 true，则代表 Fragment 需要自己处理 BackPress 事件，如果返回 false 代表 Fragment 不拦截事件，交给子 Fragment 处理，如果子 Fragment 也不处理，则由 Activity 处理。*/
  open fun handleBackPress() = false
  override fun onBackPressed() = handleBackPress() || BackHandlerHelper.handleBackPress(this)

  override fun isBaseOnWidth(): Boolean {
    return false
  }

  override fun getSizeInDp(): Float {
    return 760f
  }

  open fun showLoadingDialog(): LoadingDialog? {
    if (isLoadingDialogShowing()) {
      hideLoadingDialog()
    }
    return LoadingHandler.instance.showLoading(activity)
  }

  open fun hideLoadingDialog() {
    LoadingHandler.instance.hideLoading(this)
  }

  open fun isLoadingDialogShowing(): Boolean {
    return LoadingHandler.instance.isShowing(this)
  }

  override fun onPrimaryNavigationFragmentChanged(isPrimaryNavigationFragment: Boolean) {
    super.onPrimaryNavigationFragmentChanged(isPrimaryNavigationFragment)
    Timber.tag(TagsFactory.fragment_lifecycle)
      .i(">>>> <${javaClass.simpleName}> onPrimaryNavigationFragmentChanged: [isPrimaryNavigationFragment= $isPrimaryNavigationFragment]")
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    Timber.tag(TagsFactory.fragment_lifecycle)
      .i(">>>> <${javaClass.simpleName}> onAttach: [context= $context]")
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Timber.tag(TagsFactory.fragment_lifecycle)
      .i(">>>> <${javaClass.simpleName}> onCreate ${if (savedInstanceState != null) ": [savedInstanceState= $savedInstanceState]" else ""}")
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    Timber.tag(TagsFactory.fragment_lifecycle).i(">>>> <${javaClass.simpleName}> onCreateView")
    return super.onCreateView(inflater, container, savedInstanceState)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    Timber.tag(TagsFactory.fragment_lifecycle)
      .i(">>>> <${javaClass.simpleName}> onViewCreated ${if (savedInstanceState != null) ": [savedInstanceState= $savedInstanceState]" else ""}")
  }

  override fun onStart() {
    super.onStart()
    Timber.tag(TagsFactory.fragment_lifecycle).i(">>>> <${javaClass.simpleName}> onStart")
  }

  override fun onResume() {
    super.onResume()
    Timber.tag(TagsFactory.fragment_lifecycle).i(">>>> <${javaClass.simpleName}> onResume")
  }

  override fun onPause() {
    super.onPause()
    Timber.tag(TagsFactory.fragment_lifecycle).i(">>>> <${javaClass.simpleName}> onPause")
  }

  override fun onStop() {
    super.onStop()
    Timber.tag(TagsFactory.fragment_lifecycle).i(">>>> <${javaClass.simpleName}> onStop")
  }

  override fun onDestroy() {
    super.onDestroy()
    Timber.tag(TagsFactory.fragment_lifecycle).i(">>>> <${javaClass.simpleName}> onDestroy")
  }

  override fun onDestroyView() {
    super.onDestroyView()
    Timber.tag(TagsFactory.fragment_lifecycle).i(">>>> <${javaClass.simpleName}> onDestroyView")
  }

  override fun onDetach() {
    super.onDetach()
    Timber.tag(TagsFactory.fragment_lifecycle).i(">>>> <${javaClass.simpleName}> onDetach")
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    Timber.tag(TagsFactory.fragment_lifecycle)
      .i(">>>> <${javaClass.simpleName}> onSaveInstanceState: [outState= ${outState}]")
  }

  override fun onViewStateRestored(savedInstanceState: Bundle?) {
    super.onViewStateRestored(savedInstanceState)
    Timber.tag(TagsFactory.fragment_lifecycle)
      .i(">>>> <${javaClass.simpleName}> onViewStateRestored: [savedInstanceState= ${savedInstanceState}]")
  }

  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    Timber.tag(TagsFactory.fragment_lifecycle)
      .i(">>>> <${javaClass.simpleName}> onConfigurationChanged: [Configuration= ${newConfig}]")
  }

  override fun onHiddenChanged(hidden: Boolean) {
    super.onHiddenChanged(hidden)
    Timber.tag(TagsFactory.fragment_lifecycle)
      .i(">>>> <${javaClass.simpleName}> onHiddenChanged: [hidden= ${hidden}]")
  }
}