package com.smarternote.core.base.activity

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import com.smarternote.core.BaseApplication
import com.smarternote.core.base.delegates.BackHandlerHelper
import com.smarternote.core.config.LogTagsConfig.activity_lifecycle
import com.smarternote.core.ui.dialog.AppLoadingDialog
import com.smarternote.core.ui.dialog.AppLoadingDialogManager
import me.jessyan.autosize.internal.CustomAdapt
import timber.log.Timber
import kotlin.properties.Delegates

open class BaseActivity : AppCompatActivity(), CustomAdapt {

  private var themeOverlay: Int = -1

  private val activityResultContract by lazy {
    object : ActivityResultContract<Intent, ActivityResult>() {
      override fun createIntent(context: Context, input: Intent): Intent = input

      override fun parseResult(resultCode: Int, intent: Intent?): ActivityResult =
        ActivityResult(resultCode, intent)
    }
  }

  private val activityResultCallback by lazy {
    ActivityResultCallback<ActivityResult> {
      Timber.tag(activity_lifecycle).i(">>>> <${javaClass.simpleName}> onActivityResult [activityResult=$it]")
      onActivityResultCallback(it)
    }
  }

  protected val activityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(activityResultContract, activityResultCallback)

  fun setThemeOverlay(themeOverlay: Int) {
    this.themeOverlay = themeOverlay
  }

  /**
   * 是否按照宽度进行等比例适配 (为了保证在高宽比不同的屏幕上也能正常适配, 所以只能在宽度和高度之中选择一个作为基准进行适配)
   *
   * @return `true` 为按照宽度进行适配, `false` 为按照高度进行适配
   */
  override fun isBaseOnWidth(): Boolean {
    return false
  }

  /**
   * 这里使用 iPhoneX 的设计图, iPhone 的设计图尺寸为 375dp * 812dp(1125 x 2436) 3.0xxhdpi
   * 2436px / 3 = 812dp
   *
   *
   * 返回设计图上的设计尺寸, 单位 dp
   * [.getSizeInDp] 须配合 [.isBaseOnWidth] 使用, 规则如下:
   * 如果 [.isBaseOnWidth] 返回 `true`, [.getSizeInDp] 则应该返回设计图的总宽度
   * 如果 [.isBaseOnWidth] 返回 `false`, [.getSizeInDp] 则应该返回设计图的总高度
   * 如果您不需要自定义设计图上的设计尺寸, 想继续使用在 AndroidManifest 中填写的设计图尺寸, [.getSizeInDp] 则返回 `0`
   *
   * @return 设计图上的设计尺寸, 单位 dp
   */
  override fun getSizeInDp(): Float {
    return 760f
  }

  override fun attachBaseContext(newBase: Context) {
    if (themeOverlay != -1) {
      val contextThemeWrapper = ContextThemeWrapper(newBase, themeOverlay)
      super.attachBaseContext(contextThemeWrapper)
    } else {
      super.attachBaseContext(newBase)
    }
  }

  open fun showLoadingDialog(): AppLoadingDialog? {
    if (isLoadingDialogShowing()) hideLoadingDialog()
    return AppLoadingDialogManager.showLoadingDialog(this)
  }

  open fun hideLoadingDialog() {
    AppLoadingDialogManager.hideLoadingDialog(this)
  }

  open fun isLoadingDialogShowing(): Boolean {
    return AppLoadingDialogManager.isLoadingDialogShowing(this)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
//    ARouter.getInstance().inject(this)
    super.onCreate(savedInstanceState)
    Timber.tag(activity_lifecycle).i(">>>> <${javaClass.simpleName}> onCreate")

    onBackPressedMethod()
  }

  override fun onRestart() {
    super.onRestart()
    Timber.tag(activity_lifecycle).i(">>>> <${javaClass.simpleName}> onRestart")
  }

  override fun onStart() {
    super.onStart()
    Timber.tag(activity_lifecycle).i(">>>> <${javaClass.simpleName}> onStart")
  }

  override fun onResume() {
    super.onResume()
    Timber.tag(activity_lifecycle).i(">>>> <${javaClass.simpleName}> onResume")
    BaseApplication.instance.currentActivity = this
  }

  override fun onPause() {
    super.onPause()
    Timber.tag(activity_lifecycle).i(">>>> <${javaClass.simpleName}> onPause")
  }

  override fun onStop() {
    super.onStop()
    Timber.tag(activity_lifecycle).i(">>>> <${javaClass.simpleName}> onStop")
  }

  override fun onDestroy() {
    super.onDestroy()
    Timber.tag(activity_lifecycle).i(">>>> <${javaClass.simpleName}> onDestroy")
  }

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)
    Timber.tag(activity_lifecycle).i(">>>> <${javaClass.simpleName}> onPostCreate")
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    Timber.tag(activity_lifecycle).i(">>>> <${javaClass.simpleName}> onSaveInstanceState [outState=$outState]")
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    super.onRestoreInstanceState(savedInstanceState)
    Timber.tag(activity_lifecycle).i(">>>> <${javaClass.simpleName}> onRestoreInstanceState [savedInstanceState=$savedInstanceState]")
  }

  override fun onRequestPermissionsResult(
    requestCode: Int, permissions: Array<String?>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    Timber.tag(activity_lifecycle).i(">>>> <${javaClass.simpleName}> onRequestPermissionsResult")
  }

  override fun onResumeFragments() {
    super.onResumeFragments()
    Timber.tag(activity_lifecycle).i(">>>> <${javaClass.simpleName}> onResumeFragments")
  }

  override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    Timber.tag(activity_lifecycle).i(">>>> <${javaClass.simpleName}> onWindowFocusChanged [hasFocus=$hasFocus]")
  }

  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    Timber.tag(activity_lifecycle).i(">>>> <${javaClass.simpleName}> onConfigurationChanged [Configuration=$newConfig]")
  }

  protected open fun onActivityResultCallback(result: ActivityResult) {}

  private fun onBackPressedMethod(){
    if (Build.VERSION.SDK_INT >= 33) {
      onBackInvokedDispatcher.registerOnBackInvokedCallback(
        OnBackInvokedDispatcher.PRIORITY_DEFAULT) {
        if (BackHandlerHelper.handleBackPress(this)) {
          Timber.tag(activity_lifecycle).i(">>>> <${javaClass.simpleName}> onBackPressed() called but child fragment handle it")
        } else {
          Timber.tag(activity_lifecycle).i(">>>> <${javaClass.simpleName}> onBackPressed() finish activity")
          finish()
        }
      }
    } else {
      onBackPressedDispatcher.addCallback(
        this,
        object : OnBackPressedCallback(true) {
          override fun handleOnBackPressed() {
            if (BackHandlerHelper.handleBackPress(this@BaseActivity)) {
              Timber.tag(activity_lifecycle).i(">>>> <${javaClass.simpleName}> onBackPressed() called but child fragment handle it")
            } else {
              Timber.tag(activity_lifecycle).i(">>>> <${javaClass.simpleName}> onBackPressed() finish activity")
              finish()
            }
          }
        })
    }
  }
}