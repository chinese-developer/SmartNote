package com.smarternote.core.di.serializers

import com.google.gson.Gson
import java.lang.reflect.Type

class DataModelConverter<T>(private val type: Type) : IDataModelConverter<T> {

  private val gson by lazy {
    Gson()
  }

  override fun toJson(data: T): String {
    return gson.toJson(data)
  }

  override fun fromJson(json: String): T? {
    return gson.fromJson(json, type)
  }
}

interface IDataModelConverter<T> {
  fun fromJson(json: String): T?
  fun toJson(data: T): String
}