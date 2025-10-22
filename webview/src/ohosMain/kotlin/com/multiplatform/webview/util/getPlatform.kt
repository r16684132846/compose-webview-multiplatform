package com.multiplatform.webview.util

import ohos.system.version.SystemVersion

internal actual fun getPlatform(): Platform {
    return Platform.OHOS
}

internal actual fun getPlatformVersion(): String {
    return try {
        SystemVersion.getVersion()
    } catch (e: Exception) {
        "Unknown"
    }
}

internal actual fun getPlatformVersionDouble(): Double {
    return try {
        SystemVersion.getVersion().toDoubleOrNull() ?: 0.0
    } catch (e: Exception) {
        0.0
    }
}
