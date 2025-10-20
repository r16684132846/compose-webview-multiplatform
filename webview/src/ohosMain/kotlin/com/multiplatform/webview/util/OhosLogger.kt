package com.multiplatform.webview.util

import co.touchlab.kermit.Logger

/**
 * OHOS平台日志工具类（使用通用日志框架）
 */
internal object OhosLogger {

    fun debug(message: String) {
        Logger.d(message, tag = "WebViewMultiplatform")
    }

    fun info(message: String) {
        Logger.i(message, tag = "WebViewMultiplatform")
    }

    fun warn(message: String) {
        Logger.w(message, tag = "WebViewMultiplatform")
    }

    fun error(message: String) {
        Logger.e(message, tag = "WebViewMultiplatform")
    }
}
