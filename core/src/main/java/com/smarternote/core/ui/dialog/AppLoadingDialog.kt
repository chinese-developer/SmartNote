@file:Suppress("PropertyName", "unused")

package com.smarternote.core.ui.dialog

import android.app.Activity
import android.app.Dialog
import com.airbnb.lottie.LottieAnimationView
import com.smarternote.core.R
import java.lang.ref.SoftReference

class AppLoadingDialog internal constructor(
    activity: Activity,
    private var _animateSrc: String? = null
) : Dialog(activity, com.smarternote.themes.R.style.Widget_Dialog_AppLoadingDialog) {

    private val lottieAnimationView: LottieAnimationView by lazy { findViewById(R.id.lottile_animation_view) }
    private val ownerActivitySoft = SoftReference(activity)

    val _activity: Activity?
        get() = ownerActivitySoft.get()

    init {
        setContentView(R.layout.core_lottile_loading_dialog)
        window?.setWindowAnimations(com.smarternote.themes.R.style.Animation_WindowEnterExit_Null)
        lottieAnimationView.setAnimation(_animateSrc ?: globeAnimateSrc ?: defaultAnimateSrc)
        setDissmissType(TYPE.WEAK)
    }

    fun setAnimateAssetName(assetName: String?): AppLoadingDialog {
        if (!assetName.isNullOrEmpty()) {
            _animateSrc = assetName
            lottieAnimationView.setAnimation(_animateSrc)
        }
        return this
    }

    fun setDissmissType(type: Int) {
        when (type) {
            TYPE.WEAK -> {
                setCanceledOnTouchOutside(true)
                setCancelable(true)
            }

            TYPE.SOFT -> {
                setCanceledOnTouchOutside(false)
                setCancelable(true)
            }

            TYPE.HARD -> {
                setCanceledOnTouchOutside(false)
                setCancelable(false)
            }
        }
    }

    override fun onStart() {
        lottieAnimationView.playAnimation()
        super.onStart()
    }

    override fun onStop() {
        lottieAnimationView.cancelAnimation()
        super.onStop()
    }

    companion object {
        private const val defaultAnimateSrc = "loading.json"
        var globeAnimateSrc: String? = null
    }

    object TYPE {
        const val WEAK = 0
        const val SOFT = 1
        const val HARD = 2
    }
}
