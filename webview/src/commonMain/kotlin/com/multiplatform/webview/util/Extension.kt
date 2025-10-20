package com.multiplatform.webview.util

fun Pair<Number, Number>?.isZero(): Boolean {
    return this == null || (first == 0 && second == 0)
}

fun Pair<Number, Number>?.notZero(): Boolean {
    return !isZero()
}

/**
 * Check if the current platform is Android.
 */
internal fun Platform.isAndroid(): Boolean = this is Platform.Android

/**
 * Check if the current platform is iOS.
 */
internal fun Platform.isIOS(): Boolean = this is Platform.IOS

/**
 * Check if the current platform is Desktop.
 */
internal fun Platform.isDesktop(): Boolean = this is Platform.Desktop

/**
 * Check if the current platform is OHOS.
 */
internal fun Platform.isOHOS(): Boolean = this is Platform.OHOS