// 确保所有模块在引用插件时使用相同的插件版本，只需要在这里修改它们，适用于所有模块的更改。
// apply false: 插件在根目录的 build.gradle.kts 文件中定义，但没有应用到根项目。
plugins {
    id("com.android.application") version "8.0.0" apply false
    id("com.android.library") version "8.0.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.20" apply false
}

// buildscript 仅应用于构建脚本本身，它定义了 Gradle 在构建脚本中需要使用哪些仓库。
buildscript {
    // dependencies: 定义了 Gradle 需要下载哪些依赖项，并且通过 classpath 指定了 Gradle 插件依赖的版本。
    dependencies {
        classpath("com.android.tools.build:gradle:8.0.0")
        classpath("com.google.dagger:hilt-android-gradle-plugin:${Dependencies.hilt}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.21")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
