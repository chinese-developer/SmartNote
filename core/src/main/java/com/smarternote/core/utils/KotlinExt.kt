package com.smarternote.core.utils

import android.content.res.Resources

val Number.dp
    get() = (this.toFloat() * Resources.getSystem().displayMetrics.density).toInt()

val Number.sp
    get() = (this.toFloat() * Resources.getSystem().displayMetrics.scaledDensity).toInt()

val Number.KB: Long
    get() = this.toLong() * 1024L

val Number.MB: Long
    get() = this.KB * 1024L

val Number.GB: Long
    get() = this.MB * 1024L