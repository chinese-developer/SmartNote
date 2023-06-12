package com.smarternote.feature.sport

import java.util.Random

fun main() {

    val originalList = mutableListOf(1, 2, 3, 4, 5)

    // 每次打印的顺序都可能不同
    println(originalList.shuffled())

    // 当你向 shuffled 函数传递一个带有特定种子的 Random 对象时，
    // 它会生成一个伪随机序列，这个序列是可重复的。
    // 也就是说，当你使用相同的种子多次调用 shuffled 函数时，你会得到相同的随机排列。

    // [5, 1, 3, 2, 4]
    println(originalList.shuffled(Random(2)))
    // [5, 1, 3, 2, 4]
    println(originalList.shuffled(Random(2)))
    // [3, 4, 2, 5, 1]
    println(originalList.shuffled(Random(1)))

}