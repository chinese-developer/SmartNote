@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

android {
    namespace = "com.smarternote.core"
    compileSdk = 33

    defaultConfig {
        minSdk = 26
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        dataBinding = true
    }
    kapt {
        arguments {
            arg("AROUTER_MODULE_NAME", project.name)
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    api(project(":themes"))

    // Kotlin 标准库, 依赖项包含了一些额外的Java 8特定的扩展函数
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Dependencies.Core.kotlin}")
    // AndroidX 库中的一个核心库，它提供了许多与 Android UI 相关的类和函数
    api("androidx.appcompat:appcompat:${Dependencies.AndroidX.appcompat}")
    // 是 Android Material Components 库中的一个依赖项。它提供了一组用于创建符合 Material Design 风格的 UI 元素和组件的类和资源。
    // 如 AppBarLayout BottomNavigationView FloatingActionButton
    api("com.google.android.material:material:${Dependencies.material}")
    // AndroidX 库中的一个核心库，它提供了许多与 Android 开发相关的扩展函数和属性
    api("androidx.core:core:${Dependencies.AndroidX.coreKtx}")
    api("androidx.core:core-ktx:${Dependencies.AndroidX.coreKtx}")
    // 用于实现基于 SharedPreferences 的数据存储方式，支持数据加密和数据迁移。
    implementation("androidx.datastore:datastore-preferences:${Dependencies.AndroidX.dataStore}")
    // 包含 DataStore 通用的核心代码和 API。
    implementation("androidx.datastore:datastore-core:${Dependencies.AndroidX.dataStore}")
    // 包含了 DataStore 基于 SQLite 实现的模块，使用该模块可以支持更大的数据量，并且使用起来更加灵活和高效。
    implementation("androidx.datastore:datastore:${Dependencies.AndroidX.dataStore}")
    // Google 提供的一个 Android UI 库，它提供了一些现代化的、美观的 UI 控件和样式
    api("com.google.android.material:material:${Dependencies.UI.material}")
    api("com.google.code.gson:gson:${Dependencies.gson}")
    // Android平台上的图片加载库，由Google官方推荐
    api("com.github.bumptech.glide:glide:${Dependencies.UI.glide}")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:${Dependencies.retrofit}")
    implementation("com.squareup.retrofit2:converter-moshi:${Dependencies.retrofit}")
    implementation("com.squareup.okhttp3:logging-interceptor:${Dependencies.loggingInterceptor}")
    api("com.squareup.moshi:moshi:${Dependencies.moshi}")
    api("com.squareup.moshi:moshi-kotlin:${Dependencies.moshi}")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:${Dependencies.moshi}")

    // 开源的动画库，它使得开发者能够以一种简单的方式在移动应用中添加高质量的动画效果
    api("com.airbnb.android:lottie:${Dependencies.lottie}")
    // Android平台上的一个数据绑定库，它可以帮助开发者将应用程序的UI和数据模型进行绑定，从而方便地进行数据展示和操作。
    api("androidx.databinding:databinding-runtime:${Dependencies.UI.databinding}")

    // 日志记录
    implementation("com.jakewharton.timber:timber:${Dependencies.timber}")

    // ARouter 跨组件通信的路由框架
    api("com.alibaba:arouter-api:${Dependencies.arouter}")
    kapt("com.alibaba:arouter-compiler:${Dependencies.arouter}")

    // // Google Dagger Hilt 的 Android 集成库，用于为 Android 应用程序提供依赖注入支持
    implementation("com.google.dagger:hilt-android:${Dependencies.hilt}")
    kapt("com.google.dagger:hilt-android-compiler:${Dependencies.hilt}")

    // 今日头条屏幕适配方案 https://github.com/JessYanCoding/AndroidAutoSize
    api("com.github.JessYanCoding:AndroidAutoSize:${Dependencies.Github.autoSize}")

    // 一个强大、可扩展且易于使用的 ViewPager 指示器
    api("com.github.hackware1993:MagicIndicator:${Dependencies.Github.magicIndicator}")

}