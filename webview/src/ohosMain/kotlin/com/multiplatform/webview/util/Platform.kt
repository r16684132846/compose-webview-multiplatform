package com.multiplatform.webview.util

/**
 * Platform enumeration
 */
enum class Platform {
    ANDROID,
    IOS,
    DESKTOP,
    OHOS,
    ;

    fun isAndroid(): Boolean = this == ANDROID
    fun isIOS(): Boolean = this == IOS
    fun isDesktop(): Boolean = this == DESKTOP
    fun isOhos(): Boolean = this == OHOS
}
