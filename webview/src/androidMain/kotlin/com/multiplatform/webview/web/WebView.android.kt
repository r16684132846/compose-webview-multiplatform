package com.multiplatform.webview.web

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.multiplatform.webview.jsbridge.WebViewJsBridge

/**
 * Android WebView implementation.
 */
@Composable
actual fun ActualWebView(
    state: WebViewState,
    modifier: Modifier,
    captureBackPresses: Boolean,
    navigator: WebViewNavigator,
    webViewJsBridge: WebViewJsBridge?,
    onCreated: (NativeWebView) -> Unit,
    onDispose: (NativeWebView) -> Unit,
    factory: (WebViewFactoryParam) -> NativeWebView,
) {
    AccompanistWebView(
        state,
        modifier,
        captureBackPresses,
        navigator,
        webViewJsBridge,
        onCreated = onCreated,
        onDispose = onDispose,
        factory = { factory(WebViewFactoryParam(it)) },
    )
}

/** Android WebView factory parameters: a context. */
actual data class WebViewFactoryParam(val context: Context)

/** Default WebView factory for Android. */
actual fun defaultWebViewFactory(param: WebViewFactoryParam) = android.webkit.WebView(param.context)

/**
 * Android implementation for opening URL in system browser
 */
@Composable
actual fun openBrower(url: String) {
    // 获取当前的 Context
    val context = LocalContext.current
    // 创建 Intent
    val intent =
        android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)

    // 启动浏览器
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // Handle exception if unable to open browser
        e.printStackTrace()
    }
}