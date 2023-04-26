@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Kotlin 标准库, 依赖项包含了一些额外的Java 8特定的扩展函数
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Dependencies.Core.kotlin}")
    //  AndroidX 库中的一个核心库，它提供了许多与 Android 开发相关的扩展函数和属性
    implementation("androidx.core:core-ktx:${Dependencies.AndroidX.coreKtx}")
    //  AndroidX 库中的一个核心库，它提供了许多与 Android UI 相关的类和函数
    implementation("androidx.appcompat:appcompat:${Dependencies.AndroidX.appcompat}")
    // Google 提供的一个 Android UI 库，它提供了一些现代化的、美观的 UI 控件和样式
    implementation("com.google.android.material:material:${Dependencies.UI.material}")
}