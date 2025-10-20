plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    kotlin("multiplatform").apply(false)
    kotlin("plugin.serialization").apply(false)
    id("com.android.application").apply(false)
    id("com.android.library").apply(false)
    id("org.jetbrains.compose").apply(false)
    id("org.jetbrains.dokka")
    id("com.vanniktech.maven.publish") version "0.25.3" apply false
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1"
    id("org.jetbrains.kotlin.plugin.atomicfu") version "1.9.20"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21-KBA-006"
    id("org.jetbrains.kotlin.native.cocoapods").version("2.0.21-KBA-006").apply(false)
}


tasks.register<Copy>("setUpGitHooks") {
    group = "help"
    from("$rootDir/.hooks")
    into("$rootDir/.git/hooks")
}
