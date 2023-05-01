@file:Suppress("unused")

package com.smarternote.core.ui.dialog

import android.app.Activity
import android.os.Handler
import android.os.Looper
import java.lang.ref.WeakReference

object AppLoadingDialogManager {

    private val handler = Handler(Looper.getMainLooper())
    private val weakReferenceLoadingDialog = HashMap<Any, WeakReference<AppLoadingDialog>>()
    private val lock = Any()

    @JvmOverloads
    fun showLoadingDialog(activity: Activity? = null, tag: Any? = null): AppLoadingDialog? = synchronized(lock) {
        if (activity == null || activity.isFinishing || activity.isDestroyed) {
            return null
        }
        val key = tag?.hashCode() ?: activity.hashCode()
        val loadingDialog = weakReferenceLoadingDialog[key]?.get() ?: AppLoadingDialog(activity).also {
            weakReferenceLoadingDialog[key] = WeakReference(it)
        }
        return loadingDialog.apply { show() }
    }

    fun hideLoadingDialog(tag: Any?) {
        handler.postDelayed({
            tag?.let {
                synchronized(lock) {
                    weakReferenceLoadingDialog[tag.hashCode()]?.get()?.apply {
                        dismiss()
                    }
                }
            }
        }, 200)
    }

    fun isLoadingDialogShowing(tag: Any?): Boolean = synchronized(lock) {
        tag?.let {
            weakReferenceLoadingDialog[it.hashCode()]?.get()?.isShowing ?: false
        } ?: false
    }

    fun clear() = synchronized(lock) {
        val iterator = weakReferenceLoadingDialog.entries.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            try {
                item.value.get()?.let { weakLoadingDialog ->
                    if (weakLoadingDialog._activity == null || weakLoadingDialog._activity?.isFinishing == true || weakLoadingDialog._activity?.isDestroyed == true) {
                        weakLoadingDialog.dismiss()
                        iterator.remove()
                    }
                }
            } catch (e: Exception) {
                iterator.remove()
            }
        }
    }
}
