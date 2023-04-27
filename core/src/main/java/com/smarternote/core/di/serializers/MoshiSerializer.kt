package com.smarternote.core.di.serializers

import androidx.datastore.core.Serializer
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import okio.buffer
import okio.sink
import okio.source
import java.io.InputStream
import java.io.OutputStream

open class MoshiSerializer<T : Any>(
    moshi: Moshi,
    type: Class<T>,
    private val defaultValueProvider: () -> T
) : Serializer<T> {
    private val adapter: JsonAdapter<T> = moshi.adapter(type)

    override suspend fun readFrom(input: InputStream): T {
        return input.source().buffer().use { bufferedSource ->
            val jsonReader = JsonReader.of(bufferedSource)
            adapter.fromJson(jsonReader) ?: defaultValueProvider()
        }
    }

    override suspend fun writeTo(t: T, output: OutputStream) {
        output.sink().buffer().use { bufferedSink ->
            adapter.toJson(bufferedSink, t)
        }
    }

    override val defaultValue: T
        get() = defaultValueProvider()

    companion object {
        inline fun <reified T : Any> create(
            moshi: Moshi,
            noinline defaultValueProvider: () -> T
        ): MoshiSerializer<T> {
            return MoshiSerializer(moshi, T::class.java, defaultValueProvider)
        }
    }
}

