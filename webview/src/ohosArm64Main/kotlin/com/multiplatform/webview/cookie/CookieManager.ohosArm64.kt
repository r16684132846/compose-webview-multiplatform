package com.multiplatform.webview.cookie

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * OHOS implementation of [WebViewCookieManager].
 */
object WebViewCookieManager : CookieManager {
    private val cookieStorage = mutableMapOf<String, MutableList<Cookie>>()

    override suspend fun setCookie(
        url: String,
        cookie: Cookie,
    ) {
        require(url.isNotBlank()) { "URL cannot be null or empty" }
        require(cookie != null) { "Cookie cannot be null" }

        val domain = cookie.domain ?: extractDomainFromUrl(url)
        val cookies = cookieStorage.getOrPut(domain) { mutableListOf() }

        cookies.removeAll { it.name == cookie.name }
        cookies.add(cookie)
    }

    override suspend fun getCookies(url: String): List<Cookie> = suspendCancellableCoroutine { continuation ->
        require(url.isNotBlank()) { "URL cannot be null or empty" }

        val domain = extractDomainFromUrl(url)
        val cookies = cookieStorage[domain] ?: emptyList()

        continuation.resume(cookies.filter {
            // 路径匹配逻辑
            it.path == null || url.contains(it.path)
        })
    }

    override suspend fun removeAllCookies() = suspendCancellableCoroutine { continuation ->
        cookieStorage.clear()
        continuation.resume(Unit)
    }

    override suspend fun removeCookies(url: String) = suspendCancellableCoroutine { continuation ->
        require(url.isNotBlank()) { "URL cannot be null or empty" }

        val domain = extractDomainFromUrl(url)
        cookieStorage.remove(domain)
        continuation.resume(Unit)
    }

    private fun extractDomainFromUrl(url: String): String {
        // 域名提取
        return try {
            val noProtocol = url.substringAfter("://").substringBefore("/").trim()
            if (noProtocol.isEmpty()) url else noProtocol
        } catch (e: Exception) {
            url
        }
    }
}

/**
 * Creates a [CookieManager] instance.
 */
@Suppress("FunctionName")
actual fun WebViewCookieManager(): CookieManager = WebViewCookieManager
