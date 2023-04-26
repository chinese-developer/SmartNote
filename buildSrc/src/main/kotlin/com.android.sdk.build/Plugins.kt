@file:Suppress("unused", "PackageDirectoryMismatch")

object Plugins {

    object BuildPlugins {
        const val androidLib = "com.android.library"
        const val application = "com.android.application"
        const val kotlinAndroid = "kotlin-android"
        const val kotlinKapt = "kotlin-kapt"
        const val hilt = "dagger.hilt.android.plugin"
        const val mavenPublish = "maven-publish"
        const val navSafeArgs = "androidx.navigation.safeargs.kotlin"
        const val spotless = "com.diffplug.spotless"
    }

    // classpath plugins
    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.androidGradlePlugin}"
    const val hiltGradlePlugin = "com.google.dagger:hilt-android-gradle-plugin:${Dependencies.hilt}"
    const val kotlinGradlePlugin =
        "org.jetbrains.kotlin:kotlin-gradle-plugin:${Dependencies.Core.kotlin}"

    object Versions {
        const val androidGradlePlugin = "4.2.1"
        const val buildTools = "30.0.3"
    }
}