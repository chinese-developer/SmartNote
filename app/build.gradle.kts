@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // 这是 Android 应用程序插件，用于构建 Android 应用程序。
    id("com.android.application")
    // 这是 Kotlin Android 插件，用于支持 Kotlin 语言在 Android 应用程序中的开发和构建。
    id("org.jetbrains.kotlin.android")
    // 这是一个 Hilt 插件，用于为 Android 应用程序提供依赖注入支持。
    id("dagger.hilt.android.plugin")
    // 这是一个用于 Kotlin 注解处理的插件，允许开发者在 Kotlin 代码中使用注解，并处理它们生成相关的代码。
    id("kotlin-kapt")
}

android {
    // Android Gradle Plugin 8.0.0，这个版本中引入了新的命名空间属性，移除 AndroidManifest.xml package name。
    namespace = "com.smarternote.app"
    // 表示编译时使用的 Android SDK 版本，即使用 API level 31 的 SDK 进行编译。
    // 该值通常应该与设备运行的 Android 版本相同或更高，以确保应用程序能够在该设备上运行并使用其最新功能。
    compileSdk = 33
    // 表示使用的构建工具版本。构建是指将项目打包成可部署的APK或AAB文件，包含了应用程序代码、资源、依赖库等所有必要的文件。
    // 构建版本需要满足编译所需的SDK版本要求，否则在运行时可能会出现兼容性问题
    buildToolsVersion = "30.0.3"

    defaultConfig {
        // 这是一个唯一标识 Android 应用程序的字符串，用于在 Google Play 商店和设备上区分不同的应用程序。
        // applicationId 通常与主包名相同，但它们之间没有强制关联。
        // 您可以根据需要选择不同的 applicationId，只要确保它是唯一的。
        applicationId = "com.smarternote"
        // 应用程序可以运行的最小 Android SDK 版本
        minSdk = 26
        // 指定应用程序被编译和构建时所使用的 Android SDK 版本
        targetSdk = 33
        versionCode = 1
        versionName = "1.0.0"
        // testInstrumentationRunner 是 Android 测试框架中的一个参数，用于指定在运行 Android 单元测试时使用的测试运行器（test runner）。
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    // 配置应用程序的数字签名信
    signingConfigs {
        // 构建一个名为 release 的签名配置
        create("release") {
            // 这个参数用于指定签名文件的位置。
            storeFile = file("../keystore/sdk.keystore")
            // 这个参数用于指定 keystore 文件的密码。
            storePassword = "vsEmWMgMDgY517KL"
            // 这个参数用于指定签名密钥的别名。
            keyAlias = "smarterNote"
            // 这个参数用于指定签名密钥的密码。
            keyPassword = "WSQ22hsW2xtu8F4T"
            // 为了增强数字签名的可靠性和安全性，Android 为应用程序提供了 V1 和 V2 两种不同的签名方式。
            /**
             * 这个参数用于指定是否启用 V1 签名功能。
             * 具体来说，V1 签名是一种旧的签名方式，它使用 JarSigner 工具来对应用程序进行签名，并将签名文件附加到 APK 文件的末尾。
             * V1 签名的主要优点是兼容性较好，可以在大部分 Android 设备上正常使用。缺点是安全性相对较低，容易被破解或者篡改。
             *
             * 如果 API 级别大于 24，则不启用 V1 签名功能可以减小 APK 文件的大小，同时也可以提高应用程序的安全性，因为攻击者无法修改 V2 签名的签名信息。
             */
            enableV1Signing = false
            /**
             * 这个参数用于指定是否启用 V2 签名功能。
             * V2 签名是一种新的签名方式，它使用 APK Signature Scheme v2 来对应用程序进行签名，并将签名信息写入到 APK 文件的文件头中。
             * V2 签名的主要优点是安全性较高，可以防止应用程序被篡改或者恶意修改。
             * 缺点是兼容性较差，只能在 Android 7.0（API 级别 24）及以上的设备上使用。
             */
            enableV2Signing = true
        }
    }

    buildTypes {
        // 这个方法用于获取名为 "release" 的 BuildType 对象，它是用于构建发布版应用程序的。
        getByName("release") {
            resValue("string", "build_type", "release")
            // 这个参数用于指定签名配置，即应用程序的数字签名信息。在这里，它被设置为 "release" 签名配置，表示应用程序发布版需要进行数字签名。
            signingConfig = signingConfigs.getByName("release")
            // 这个参数用于指定是否启用代码混淆功能。在这里，它被设置为 false，表示不启用代码混淆。
            isMinifyEnabled = false
            // 这个参数用于指定是否启用资源压缩功能。在这里，它被设置为 true，表示启用资源压缩。
            // 启用资源压缩可以帮助减小 APK 文件大小，从而提高应用程序的启动速度和用户体验，但可能会对构建过程造成一定的性能影响。
            isShrinkResources = false
            // 这个参数用于指定代码混淆的规则文件。在这里，它指定了两个规则文件，分别是 proguard-android-optimize.txt 和 proguard-rules.pro，用于对应用程序的代码进行混淆和优化。
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            resValue("string", "build_type", "debug")
            isDebuggable = true
        }
    }

    testBuildType = "debug"

    compileOptions {
        // 这个参数用于指定 Java 源代码的兼容性版本。在这里，它被设置为 Java 17，表示 Java 源代码是用 Java 17 编写的。
        sourceCompatibility = JavaVersion.VERSION_17
        // 这个参数用于指定 Java 目标代码的兼容性版本。在这里，它也被设置为 Java 17，表示编译出的 Java 代码是针对 Java 17 进行优化的。
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        // Kotlin 代码编译时所使用的目标 JVM 版本，编译出的代码可以在支持 Java 17 的 JVM 上运行。
        jvmTarget = "17"
    }
    buildFeatures {
        // 这个参数用于启用数据绑定功能，数据绑定是一种用于简化 Android 应用程序中视图和数据绑定的技术，通过数据绑定，可以在视图中直接使用应用程序中的数据，从而简化了应用程序的代码。
        dataBinding = true
    }
    // 这个配置确保了ARouter可以识别每个模块并生成相应的路由表
//    kapt {
//        arguments {
//            arg("AROUTER_MODULE_NAME", project.name)
//        }
//    }
}

dependencies {
    // 通过 api 依赖，只会访问到被依赖模块中的 api 依赖项，而无法访问 implementation。同时 api 依赖也会传递给依赖 app 模块的其他模块。
    // 通过 implementation 依赖，会访问到被依赖模块中的 implementation 和 api 依赖项。
    implementation(project(":core"))
    implementation(project(":themes"))
    implementation(project(":feature_sport"))

    // Google Dagger Hilt 的 Android 集成库，用于为 Android 应用程序提供依赖注入支持
    implementation("com.google.dagger:hilt-android:${Dependencies.hilt}")
    // AndroidX 库中的一个核心库，它提供了许多与 Android 开发相关的扩展函数和属性
    implementation("androidx.core:core-ktx:${Dependencies.AndroidX.coreKtx}")

    // kapt 是 Kotlin 注解处理器的缩写。在编译时，kapt 将会处理使用了 Hilt 相关注解的 Kotlin 代码，并生成相应的依赖注入相关的代码。
    kapt("com.google.dagger:hilt-android-compiler:${Dependencies.hilt}")
//    kapt("com.alibaba:arouter-compiler:${Dependencies.arouter}")

}

/**
 * 这段代码是在 Gradle 中配置 Kotlin 编译器选项的一部分。它的作用是设置 Kotlin 代码编译时所使用的目标 JVM 版本。
 * 具体来说，这段代码将 jvmTarget 参数设置为 17，表示编译出的代码可以在支持 Java 17 的 JVM 上运行。
 * 这个参数会应用到所有类型为 KotlinCompile 的 Gradle 任务中，它将设置在编译 Kotlin 代码时所使用的选项。
 *
 * 通过设置这个参数，可以确保编译出的 Kotlin 代码具有更好的兼容性和可靠性，并且可以在更多的 JVM 上运行。
 * 由于不同的 JVM 版本对 Kotlin 代码的支持程度不同，因此，需要根据具体情况来设置 jvmTarget 参数，以确保代码的兼容性和稳定性。
 */
tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}
