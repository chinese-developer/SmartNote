package com.smarternote.feature.sport

import com.squareup.moshi.JsonClass
import java.io.Serializable

object DataCreator {

    val titles = mutableListOf<Title>().apply {
      repeat(10) {
          add(Title(id = "$it", name = "Number $it"))
      }
    }

}

@JsonClass(generateAdapter=true)
data class Title(
    val id: String,
    val name: String
): Serializable