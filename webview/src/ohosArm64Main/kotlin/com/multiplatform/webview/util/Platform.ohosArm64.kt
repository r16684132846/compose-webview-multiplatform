package com.multiplatform.webview.util

/**
 * Get the current platform.
 */
internal actual fun getPlatform(): Platform {
    return Platform.OHOS
}

internal actual fun getPlatformVersion(): String {
    return "OpenHarmony"
}

internal actual fun getPlatformVersionDouble(): Double {
    return 0.0
}
