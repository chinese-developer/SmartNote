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

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {
    // AndroidX 库中的一个核心库，它提供了许多与 Android UI 相关的类和函数
    implementation("androidx.appcompat:appcompat:${Dependencies.AndroidX.appcompat}")
    // Android平台上的图片加载库，由Google官方推荐
    implementation("com.github.bumptech.glide:glide:${Dependencies.UI.glide}")
    kapt("com.com.github.bumptech.glide:compiler:${Dependencies.UI.glide}")
    // Android平台上的一个数据绑定库，它可以帮助开发者将应用程序的UI和数据模型进行绑定，从而方便地进行数据展示和操作。
    implementation("androidx.databinding:databinding-runtime:${Dependencies.UI.databinding}")
}