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
    api(project(":core"))

    kapt("com.alibaba:arouter-compiler:${Dependencies.arouter}")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:${Dependencies.moshi}")
    implementation("com.google.dagger:hilt-android:${Dependencies.hilt}")
    kapt("com.google.dagger:hilt-android-compiler:${Dependencies.hilt}")

    // BRVAH
    implementation("io.github.cymchad:BaseRecyclerViewAdapterHelper:4.0.0-beta06")
}