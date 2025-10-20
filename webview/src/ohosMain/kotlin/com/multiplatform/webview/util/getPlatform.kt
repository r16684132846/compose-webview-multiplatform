package com.multiplatform.webview.util

import ohos.system.version.SystemVersion

internal actual fun getPlatform(): Platform {
    return Platform.OHOS
}

internal actual fun getPlatformVersion(): String {
    // OHOS平台版本获取逻辑需要根据实际OHOS SDK实现
    return SystemVersion.getApiVersion()
}

internal actual fun getPlatformVersionDouble(): Double {
    // OHOS平台版本获取逻辑需要根据实际OHOS SDK实现
    return SystemVersion.getApiVersion().toDoubleOrNull() ?: 0.0
}
