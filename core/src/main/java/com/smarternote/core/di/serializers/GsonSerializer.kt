package com.smarternote.core.di.serializers

import androidx.datastore.core.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import kotlin.coroutines.resumeWithException

abstract class GsonSerializer<T>(private val converter: DataModelConverter<T>) : Serializer<T?> {

    override val defaultValue: T?
        get() = null

    override suspend fun readFrom(input: InputStream): T? {
        return withContext(Dispatchers.IO) {
            converter.fromJson(input.bufferedReader().readText()) ?: defaultValue
        }
    }

    override suspend fun writeTo(t: T?, output: OutputStream) {
        withContext(Dispatchers.IO) {
            if (t != null) {
                output.suspendWrite(converter.toJson(t).toByteArray())
            }
        }
    }

    // 扩展函数，将 OutputStream.write() 转换为挂起函数, 解决警告问题
    private suspend fun OutputStream.suspendWrite(data: ByteArray) = suspendCancellableCoroutine { cont ->
        try {
            write(data)
            @Suppress("OPT_IN_USAGE")
            cont.resume(Unit) {}
        } catch (e: Exception) {
            e.printStackTrace()
            cont.resumeWithException(e)
        }
    }
}