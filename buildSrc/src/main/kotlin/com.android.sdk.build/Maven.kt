@file:Suppress("unused", "PackageDirectoryMismatch")

object Maven {

  const val releaseUrl = "http://172.20.13.100:8081/repository/com.gsmc.android/"
  const val snapshotUrl = "http://172.20.13.100:8081/repository/com.gsmc.android/"
  const val userName = "android"
  const val password = "123456"

  object SportSDK {

    // 快照包后缀加 `-SNAPSHOT`
    const val version = "5.1.58.18"
    const val versionSnapshot = "1.0.0-SNAPSHOT"
    const val artifactId = "sport-sdk"
    const val groupId = "com.gsmc.android"
  }
}