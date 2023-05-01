@file:Suppress("UnstableApiUsage")

/**
 * 用于管理 Gradle 插件的版本和来源。
 * 在该代码块内，可以定义一组 Gradle 插件仓库，从这些仓库中搜索和下载所需的插件。
 * 这可以避免在每个 Gradle 构建文件中单独配置插件仓库的麻烦。
 * 同时，所有的子项目都可以使用相同的插件仓库配置，避免了不同子项目中的插件仓库配置不一致导致的问题。
 */
pluginManagement {
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
    maven(url = "https://jitpack.io")
  }
}

rootProject.name = "SmartNote"
include(":app")
include(":core")
include(":themes")
include(":feature_sport")
