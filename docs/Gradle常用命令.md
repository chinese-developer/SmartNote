# 以下是一些常用的 Gradle 命令及其相应的错误定位方法

## ./gradlew assembleDebug
该命令将构建 Debug 版本的应用程序，并将 APK 文件生成到项目的 app/build/outputs/apk/debug 目录下。
如果在构建过程中出现错误，Gradle 将输出详细的错误信息，其中包含了出现问题的文件、行数以及错误类型等信息。

## ./gradlew compileDebugSources
该命令将编译 Debug 版本的源代码，并在控制台输出编译过程中的详细信息。
如果出现编译错误，Gradle 将输出错误信息，其中包含了出现问题的文件、行数以及错误类型等信息。

## ./gradlew lintDebug
该命令将运行 Lint 工具来分析项目代码，并输出分析结果。
如果 Lint 工具发现了错误，Gradle 将输出错误信息，其中包含了出现问题的文件、行数以及错误类型等信息。