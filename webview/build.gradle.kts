plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose") version "1.6.1-KBA-007"
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.atomicfu")
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21-KBA-006"
    id("maven-publish")
    id("signing")
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
//    targetHierarchy.default()
    applyDefaultHierarchyTemplate()

    androidTarget {
        compilations.all {
            compileTaskProvider.get().compilerOptions {
                jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
            }
        }
    }

//    jvm("desktop")

//    listOf(
//        iosX64(),
//        iosArm64(),
//        iosSimulatorArm64(),
//    ).forEach {
//        it.binaries.framework {
//            baseName = "shared"
//        }
//    }

    // 添加OHOS支持
    ohosArm64()

    sourceSets {
        val coroutinesVersion = extra["coroutines.version"] as String

        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                implementation("co.touchlab:kermit:2.0.3")
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
                api("androidx.webkit:webkit:1.7.0")
            }
        }
//        val desktopMain by getting {
//            dependencies {
//                implementation(compose.desktop.common)
//                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:$coroutinesVersion")
//            }
//        }

        // 添加OHOS源集
        val ohosArm64Main by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
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

            pom {
                name.set(project.property("POM_NAME").toString())
                description.set(project.property("POM_DESCRIPTION").toString())
                url.set(project.property("POM_URL").toString())

                licenses {
                    license {
                        name.set(project.property("POM_LICENCE_NAME").toString())
                        url.set(project.property("POM_LICENCE_URL").toString())
                        distribution.set(project.property("POM_LICENCE_DIST").toString())
                    }
                }

                developers {
                    developer {
                        id.set(project.property("POM_DEVELOPER_ID").toString())
                        name.set(project.property("POM_DEVELOPER_NAME").toString())
                        url.set(project.property("POM_DEVELOPER_URL").toString())
                    }
                }

                scm {
                    url.set(project.property("POM_SCM_URL").toString())
                    connection.set(project.property("POM_SCM_CONNECTION").toString())
                    developerConnection.set(project.property("POM_SCM_DEV_CONNECTION").toString())
                }
            }
        }
    }

    repositories {
        maven {
            name = "Local"
            url = uri(layout.buildDirectory.dir("repo"))
        }

        maven {
            isAllowInsecureProtocol = true
            name = "Nenus"
            url = uri("http://maven.cloud.cicoe.net/repository/kmp/")
            credentials {
                username = "kmp2"
                password = "notekmp1504"
            }
        }
    }
}