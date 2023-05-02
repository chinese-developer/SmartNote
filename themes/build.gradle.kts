@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    namespace = "com.smarternote.themes"
    compileSdk = 33

    defaultConfig {
        minSdk = 26
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
}

dependencies {
    // AndroidX 库中的一个核心库，它提供了许多与 Android UI 相关的类和函数
    implementation("androidx.appcompat:appcompat:${Dependencies.AndroidX.appcompat}")
    // Google 提供的一个 Android UI 库，它提供了一些现代化的、美观的 UI 控件和样式
    implementation("com.google.android.material:material:${Dependencies.UI.material}")
    // Android平台上的图片加载库，由Google官方推荐
    implementation("com.github.bumptech.glide:glide:${Dependencies.UI.glide}")
    // AndroidX 库中的一个核心库，它提供了许多与 Android 开发相关的扩展函数和属性
    implementation("androidx.core:core-ktx:${Dependencies.AndroidX.coreKtx}")
    // Android平台上的一个数据绑定库，它可以帮助开发者将应用程序的UI和数据模型进行绑定，从而方便地进行数据展示和操作。
    implementation("androidx.databinding:databinding-runtime:${Dependencies.UI.databinding}")
    // 开源的动画库，它使得开发者能够以一种简单的方式在移动应用中添加高质量的动画效果
    implementation("com.airbnb.android:lottie:${Dependencies.lottie}")
}