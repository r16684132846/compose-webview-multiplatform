package com.multiplatform.webview.web

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.ArkUIView
import androidx.compose.ui.interop.InteropContainer
import androidx.compose.ui.napi.asString
import androidx.compose.ui.napi.js
import com.multiplatform.webview.jsbridge.WebViewJsBridge
import com.multiplatform.webview.util.KLogger
import kotlinx.cinterop.ExperimentalForeignApi
import platform.ohos.napi_env

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun ActualWebView(
    state: WebViewState,
    modifier: Modifier,
    captureBackPresses: Boolean,
    navigator: WebViewNavigator,
    webViewJsBridge: WebViewJsBridge?,
    onCreated: (NativeWebView) -> Unit,
    onDispose: (NativeWebView) -> Unit,
    factory: (WebViewFactoryParam) -> NativeWebView
) {
    val webView = remember { factory(WebViewFactoryParam()) }
    var ohosWebView by remember { mutableStateOf<OHOSWebView?>(null) }

    LaunchedEffect(webView, navigator, state) {
        val ohosWebViewInstance = OHOSWebView(webView, this, webViewJsBridge)
        ohosWebView = ohosWebViewInstance
        state.webView = ohosWebViewInstance
        webViewJsBridge?.webView = ohosWebViewInstance
        onCreated(webView)
    }

    // 获取当前要加载的URL
    var currentUrl = when (val content = state.content) {
        is WebContent.Url -> content.url
        is WebContent.Data -> "data:text/html;charset=utf-8;base64," + content.data.encodeToByteArray()
            .joinToString("")

        else -> "about:blank"
    }

    // 获取额外的HTTP头
    val additionalHttpHeaders = when (val content = state.content) {
        is WebContent.Url -> content.additionalHttpHeaders
        else -> emptyMap()
    }

    Box(modifier = modifier) {
        ArkUIView(
            name = "webview",
            modifier = Modifier.fillMaxSize(),
            parameter = js {
                "url"(currentUrl)
                "enableJavaScript"(state.webSettings.isJavaScriptEnabled)
                "javascriptEnabled"(state.webSettings.isJavaScriptEnabled)
                "domStorageEnabled"(state.webSettings.androidWebSettings.domStorageEnabled)
                state.webSettings.customUserAgentString?.let { userAgent ->
                    "userAgent"(userAgent)
                }
                "userAgent"("Mozilla/5.0 (OpenHarmony) AppleWebKit/537.36 (KHTML, like Gecko) Version/9.0 Mobile Safari/537.36")
                "databaseEnabled"(true)
                "cacheMode"("default")
                if (additionalHttpHeaders.isNotEmpty()) {
                    val headersJson =
                        additionalHttpHeaders.entries.joinToString(",", "{", "}") { (key, value) ->
                            "\"$key\":\"$value\""
                        }
                    "additionalHttpHeaders"(headersJson)
                }
                "visibility"(if (state.isLoading) "hidden" else "visible")
            },
            onCreate = { webViewInstance ->
                ohosWebView?.initWebView()
                KLogger.d { "OHOS WebView created" }
            },
            onRelease = { webViewInstance ->
                KLogger.d { "OHOS WebView released" }
                onDispose(webView)
            },
            update = { params ->
                val urlValue = params["url"]
                if (urlValue != null) {
                    val urlString = urlValue.asString()?.toString()
                    if (urlString != null) {
                        // 更新当前URL
                        currentUrl = urlString
                    }
                }

                // 处理页面加载完成事件
                params["onPageFinished"]?.let {
                    state.loadingState = LoadingState.Finished
                }

                // 处理页面开始加载事件
                params["onPageStarted"]?.let {
                    // 如果有进度信息，可以在这里获取
                    val progressValue = params["progress"]?.asString()?.toFloatOrNull()
                    state.loadingState = LoadingState.Loading(progressValue ?: 0f)
                }

                // 处理错误事件
                params["onError"]?.let { errorValue ->
                    val errorMessage = errorValue.asString() ?: "Unknown error"
                    state.errorsForCurrentRequest.add(WebViewError(-1, errorMessage)) // 添加到错误列表
                }
            },
            interactive = true,
            container = InteropContainer.BACK
        )
    }
}


@OptIn(ExperimentalForeignApi::class)
private fun getNapiEnv(): napi_env? {
    return null
}


/**
 * Platform specific default WebView factory function. This can be called from
 * a custom factory function for any platforms that don't need to be customized.
 */
actual fun defaultWebViewFactory(param: WebViewFactoryParam): NativeWebView {
    return OhosWebView()
}

/**
 * Platform specific parameters given to the WebView factory function. This is a
 * data class containing one or more platform-specific values necessary to
 * create a platform-specific WebView:
 *   - On Android, this contains a `Context` object
 *   - On iOS, this contains a `WKWebViewConfiguration` object created from the
 *     provided WebSettings
 *   - On Desktop, this contains the WebViewState, the KCEFClient, and the
 *     loaded file content (if a file, otherwise, an empty string)
 */
actual class WebViewFactoryParam