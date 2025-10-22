package com.multiplatform.webview.cookie

import com.multiplatform.webview.util.KLogger
import ohos.app.Context
import ohos.web.webview.WebCookieManager
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * OHOS implementation of [CookieManager]
 */
object OhosCookieManager : CookieManager {
    override suspend fun setCookie(url: String, cookie: Cookie) {
        KLogger.d { "OHOS设置Cookie: $url, ${cookie.name}=${cookie.value}" }
        try {
            // 使用OHOS WebCookieManager设置cookie
            // 将Cookie对象转换为字符串格式
            val cookieString = cookie.toString()
            WebCookieManager.configCookieSync(url, cookieString)
            KLogger.d { "OHOS设置Cookie成功: $url, ${cookie.name}=${cookie.value}" }
        } catch (e: Exception) {
            KLogger.e { "OHOS设置Cookie失败: $url, ${cookie.name}, 错误: ${e.message}" }
            throw e
        }
    }

    override suspend fun getCookies(url: String): List<Cookie> {
        KLogger.d { "OHOS获取Cookie: $url" }
        return suspendCoroutine { continuation ->
            try {
                // 使用OHOS WebCookieManager获取cookie
                val cookieString = WebCookieManager.fetchCookieSync(url)
                // 解析cookie字符串为Cookie对象列表
                val cookies = parseCookieString(cookieString)
                KLogger.d { "OHOS获取Cookie成功: $url, 数量: ${cookies.size}" }
                continuation.resume(cookies)
            } catch (e: Exception) {
                KLogger.e { "OHOS获取Cookie失败: $url, 错误: ${e.message}" }
                continuation.resume(emptyList())
            }
        }
    }

    override suspend fun removeCookie(url: String, cookie: Cookie) {
        KLogger.d { "OHOS移除Cookie: $url, ${cookie.name}" }
        try {
            // OHOS不直接支持删除单个cookie，我们可以通过设置空值的方式来"删除"它
            val emptyCookie = cookie.copy(value = "")
            val cookieString = emptyCookie.toString()
            WebCookieManager.configCookieSync(url, cookieString)
            KLogger.d { "OHOS移除Cookie成功: $url, ${cookie.name}" }
        } catch (e: Exception) {
            KLogger.e { "OHOS移除Cookie失败: $url, ${cookie.name}, 错误: ${e.message}" }
            throw e
        }
    }

    override suspend fun removeAllCookies() {
        KLogger.d { "OHOS移除所有Cookie" }
        try {
            WebCookieManager.removeAllCookies()
            KLogger.d { "OHOS移除所有Cookie成功" }
        } catch (e: Exception) {
            KLogger.e { "OHOS移除所有Cookie失败: ${e.message}" }
            throw e
        }
    }

    override suspend fun removeCookies(url: String) {
        KLogger.d { "OHOS移除URL的所有Cookie: $url" }
        try {
            // 清除特定URL的所有cookie
            // 通过设置空cookie字符串来实现
            WebCookieManager.configCookieSync(url, "")
            KLogger.d { "OHOS移除URL的所有Cookie成功: $url" }
        } catch (e: Exception) {
            KLogger.e { "OHOS移除URL的所有Cookie失败: $url, 错误: ${e.message}" }
            throw e
        }
    }

    /**
     * 解析从OHOS WebCookieManager获取的cookie字符串
     */
    private fun parseCookieString(cookieString: String): List<Cookie> {
        if (cookieString.isBlank()) return emptyList()

        val cookies = mutableListOf<Cookie>()
        val cookiePairs = cookieString.split(";")

        for (pair in cookiePairs) {
            val trimmedPair = pair.trim()
            val equalIndex = trimmedPair.indexOf('=')
            if (equalIndex > 0) {
                val name = trimmedPair.substring(0, equalIndex)
                val value = trimmedPair.substring(equalIndex + 1)
                cookies.add(Cookie(name = name, value = value))
            } else if (equalIndex == 0) {
                // 只有值没有名称的cookie
                val value = trimmedPair.substring(1)
                cookies.add(Cookie(name = "", value = value))
            } else {
                // 只有名称没有值的cookie
                cookies.add(Cookie(name = trimmedPair, value = ""))
            }
        }

        return cookies
    }
}

/**
 * OHOS平台的Cookie管理器工厂函数
 */
@Suppress("FunctionName") // Builder Function
actual fun WebViewCookieManager(): CookieManager = OhosCookieManager


import java.text.SimpleDateFormat
import java.util.*

actual fun getCookieExpirationDate(expiresDate: Long): String {
    val sdf = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("GMT")
    }
    return sdf.format(Date(expiresDate))
}
