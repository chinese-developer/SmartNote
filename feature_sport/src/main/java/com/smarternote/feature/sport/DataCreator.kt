package com.smarternote.feature.sport

import com.squareup.moshi.JsonClass
import java.io.Serializable

object DataCreator {

    val titles = mutableListOf<Title>().apply {
      repeat(10) {
          val childs = mutableListOf<String>()
          repeat(10) { position ->
              childs.add("child[$position]")
          }
          add(Title(id = "$it", name = "Number $it", childs = childs))
      }
    }

}

@JsonClass(generateAdapter=true)
data class Title(
    val id: String,
    val name: String,
    val childs: List<String>
): Serializable