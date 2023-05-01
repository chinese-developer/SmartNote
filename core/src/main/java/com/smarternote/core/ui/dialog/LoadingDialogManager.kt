package com.smarternote.core.ui.dialog

import android.app.Activity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

object LoadingDialogManager {
    private val loadingMap = HashMap<Any, WeakReference<LoadingDialog>>()
    private val lock = Any()

    @JvmOverloads
    fun showLoading(activity: Activity? = null, tag: Any? = null): LoadingDialog? = synchronized(lock) {
        if (activity == null || activity.isFinishing || activity.isDestroyed) {
            return null
        }
        val mtag = tag?.hashCode() ?: activity.hashCode()
        val loadingDialog = loadingMap[mtag]?.get() ?: LoadingDialog(activity).also {
            loadingMap[mtag] = WeakReference(it)
        }
        return loadingDialog.apply { show() }
    }

    fun hideLoading(tag: Any?) {
        Observable.timer(200, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                synchronized(lock) {
                    tag?.let {
                        loadingMap[it.hashCode()]?.get()?.apply { dismiss() }
                    }
                }
            }
    }

    fun isShowing(tag: Any?): Boolean = synchronized(lock) {
        tag?.let {
            loadingMap[it.hashCode()]?.get()?.isShowing ?: false
        } ?: false
    }

    fun clearLoading() = synchronized(lock) {
        val iterator = loadingMap.entries.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            try {
                item.value.get()?.let { ld ->
                    if (ld.activity == null || ld.activity?.isFinishing == true || ld.activity?.isDestroyed == true) {
                        ld.dismiss()
                        iterator.remove()
                    }
                }
            } catch (e: Exception) {
                iterator.remove()
            }
        }
    }
}
