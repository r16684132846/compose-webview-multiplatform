rootProject.name = "compose-webview-multiplatform"

//include(":sample:androidApp")
include(":webview")
//include(":sample:desktopApp")
//include(":sample:shared")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://mirrors.tencent.com/nexus/repository/maven-public")
        maven("https://mirrors.tencent.com/nexus/repository/maven-tencent")
        // 添加 Kotlin 官方仓库
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
        maven {
            isAllowInsecureProtocol = true
            name = "Nenus"
            setUrl("http://maven.cloud.cicoe.net/repository/kmp/")
            credentials {
                username = "kmp2"
                password = "notekmp1504"
            }
        }
        // 添加OHOS插件仓库
        maven {
            setUrl("https://repo.huaweicloud.com/repository/maven/")
        }
        maven {
            setUrl("https://mirrors.huaweicloud.com/repository/maven/")
        }
    }

    plugins {
        val kotlinVersion = extra["kotlin.version"] as String
        val agpVersion = extra["agp.version"] as String
        val composeVersion = extra["compose.version"] as String

        kotlin("jvm").version(kotlinVersion)
//        kotlin("multiplatform").version(kotlinVersion)
        kotlin("plugin.serialization").version(kotlinVersion)
        kotlin("android").version(kotlinVersion)

        id("org.jetbrains.kotlin.plugin.compose").version(kotlinVersion)

        id("com.android.application").version(agpVersion)
        id("com.android.library").version(agpVersion)

        id("org.jetbrains.compose").version(composeVersion)
        id("org.jetbrains.dokka").version("1.9.0")

        // 添加OHOS插件
        id("org.jetbrains.kotlin.native.cocoapods").version(kotlinVersion)
        id("org.jetbrains.kotlin.multiplatform").version(kotlinVersion)
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven { url = uri("https://jitpack.io") }
        maven("https://mirrors.tencent.com/nexus/repository/maven-public")
        maven("https://mirrors.tencent.com/nexus/repository/maven-tencent")
        // 添加 Kotlin 官方仓库
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
        maven {
            isAllowInsecureProtocol = true
            name = "Nenus"
            setUrl("http://maven.cloud.cicoe.net/repository/kmp/")
            credentials {
                username = "kmp2"
                password = "notekmp1504"
            }
        }
        // 添加OHOS依赖仓库
        maven {
            setUrl("https://repo.huaweicloud.com/repository/maven/")
        }
        maven {
            setUrl("https://mirrors.huaweicloud.com/repository/maven/")
        }
    }
}
include(":shared")
