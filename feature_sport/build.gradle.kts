@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

android {
    namespace = "com.smarternote.feature.sport"
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
//    kapt {
//        arguments {
//            arg("AROUTER_MODULE_NAME", project.name)
//        }
//    }
}

dependencies {
    implementation(project(":core"))

    // AndroidX 库中的一个核心库，它提供了许多与 Android 开发相关的扩展函数和属性
    implementation("androidx.core:core-ktx:${Dependencies.AndroidX.coreKtx}")

    // ARouter 跨组件通信的路由框架
//    kapt("com.alibaba:arouter-compiler:${Dependencies.arouter}")
}