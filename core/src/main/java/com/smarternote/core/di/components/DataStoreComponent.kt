@file:Suppress("UNCHECKED_CAST")

package com.smarternote.core.di.components

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import com.smarternote.core.di.BaseAppComponent
import com.smarternote.core.di.serializers.MoshiSerializer
import com.squareup.moshi.Moshi
import java.io.File

class DataStoreComponent(
    private val context: Context
) : BaseAppComponent() {

    private val directoryName = "datastore_core"
    private val moshi: Moshi by lazy { Moshi.Builder().build() }
    private val dataStores = mutableMapOf<Class<*>, DataStore<*>>()

    override fun init() {
        // No initialization needed here, as DataStores are created on demand
    }

    fun <T : Any> getDataStore(clazz: Class<T>): DataStore<T> {
        return dataStores.getOrPut(clazz) {
            val serializer = MoshiSerializer.create(moshi) { clazz }
            context.createDataStore(clazz.simpleName, serializer)
        } as DataStore<T>
    }

    fun <T> Context.createDataStore(
        fileName: String,
        serializer: Serializer<T>,
        produceFile: (File) -> File = { File(it, directoryName) }
    ): DataStore<T> {
        val parentFile = produceFile(filesDir)
        if (!parentFile.exists()) {
            parentFile.mkdirs()
        }
        val file = File(parentFile, fileName)
        return DataStoreFactory.create(serializer) {
            file
        }
    }
}
