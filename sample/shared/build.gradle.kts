plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose") version "1.6.1-KBA-007"
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.atomicfu")
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21-KBA-006"
    id("maven-publish")
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
//    targetHierarchy.default()
    applyDefaultHierarchyTemplate()

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

//    jvm("desktop")

    // 添加OHOS支持
    ohosArm64(){
        binaries{
            all {
                linkerOpts("-lhilog_ndk.z",
                    "${System.getenv("OHOS_SDK_HOME")}/native/llvm/lib/aarch64-linux-ohos/libunwind.a")
            }
        }
    }

//    listOf(
//        iosX64(),
//        iosArm64(),
//        iosSimulatorArm64(),
//    ).forEach {
//        it.binaries.framework {
//            baseName = "shared"
//        }
//    }

    sourceSets {
        val coroutinesVersion = extra["coroutines.version"] as String
        val voyagerVersion = "1.0.0-rc10"

        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                implementation("co.touchlab:kermit:2.0.5-OHOS-001")
                api(project(":webview"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3-OHOS-001")
                implementation("org.jetbrains.kotlinx:atomicfu:0.23.2-OHOS-008")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-OHOS-002")
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.activity:activity-compose:1.8.2")
                api("androidx.appcompat:appcompat:1.6.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
            }
        }
//        val desktopMain by getting {
//            dependencies {
//                implementation(compose.desktop.common)
//                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:$coroutinesVersion")
//            }
//        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        // 添加OHOS源集
        val ohosArm64Main by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2-OHOS-007")
            }
        }

    }
}

android {
    namespace = "com.kevinnzou.sample"
    compileSdk = 34

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
}
// 配置发布
publishing {
    publications {
        withType<MavenPublication> {
            groupId = project.property("GROUP").toString()
            artifactId = project.property("POM_ARTIFACT_ID").toString()
            version = project.property("VERSION_NAME").toString()
        }
    }

    repositories {
        maven {
            name = "Local"
            url = uri(layout.buildDirectory.dir("repo"))
        }
    }
}