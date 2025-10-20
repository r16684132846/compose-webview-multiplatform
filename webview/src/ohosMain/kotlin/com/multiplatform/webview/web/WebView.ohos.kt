package com.multiplatform.webview.web

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.multiplatform.webview.jsbridge.WebViewJsBridge
import ohos.agp.components.ComponentContainer
import ohos.agp.components.StackLayout
import ohos.app.Context

/**
 * OHOS WebView implementation.
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
    OhosWebView(
        state = state,
        modifier = modifier,
        captureBackPresses = captureBackPresses,
        navigator = navigator,
        webViewJsBridge = webViewJsBridge,
        onCreated = onCreated,
        onDispose = onDispose,
        factory = factory,
    )
}

/** OHOS WebView factory parameters. */
actual data class WebViewFactoryParam(val context: Context)

/** Default WebView factory for OHOS. */
actual fun defaultWebViewFactory(param: WebViewFactoryParam): NativeWebView {
    // 实现OHOS WebView工厂方法
    return OhosIWebView(param.context)
}
