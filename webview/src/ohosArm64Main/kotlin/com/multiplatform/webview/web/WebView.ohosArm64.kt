package com.multiplatform.webview.web

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multiplatform.webview.jsbridge.WebViewJsBridge
import com.multiplatform.webview.util.KLogger
import kotlinx.coroutines.delay

/**
 * Platform specific parameters given to the WebView factory function. This is a
 * data class containing one or more platform-specific values necessary to
 * create a platform-specific WebView:
 *   - On Android, this contains a `Context` object
 *   - On iOS, this contains a `WKWebViewConfiguration` object created from the
 *     provided WebSettings
 *   - On Desktop, this contains the WebViewState, the KCEFClient, and the
 *     loaded file content (if a file, otherwise, an empty string)
 *   - On OHOS, this contains the context needed to create WebView
 */
actual class WebViewFactoryParam(val context: Any? = null)

/**
 * Platform specific default WebView factory function. This can be called from
 * a custom factory function for any platforms that don't need to be customized.
 */
actual fun defaultWebViewFactory(param: WebViewFactoryParam): NativeWebView {
    // 创建纯Kotlin实现的WebView实例
    return OhosWebView()
}

/**
 * Pure Kotlin implementation of a WebView-like component for OHOS
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
    factory: (WebViewFactoryParam) -> NativeWebView
) {
    val webView = remember { factory(WebViewFactoryParam()) }
    var ohosWebView by remember { mutableStateOf<OHOSWebView?>(null) }
    var pageContent by remember { mutableStateOf<String>("") }
    var isLoading by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(webView, navigator, state) {
        val ohosWebViewInstance = OHOSWebView(webView, this, webViewJsBridge)
        ohosWebView = ohosWebViewInstance
        state.webView = ohosWebViewInstance
        webViewJsBridge?.webView = ohosWebViewInstance
        onCreated(webView)

        // 根据初始状态加载内容
        when (val content = state.content) {
            is WebContent.Url -> {
                loadUrl(content.url, state, navigator)
            }
            is WebContent.Data -> {
                pageContent = content.data
                state.pageTitle = "HTML Content"
                state.lastLoadedUrl = "data:text/html;charset=utf-8,"
            }
            is WebContent.File -> {
                // 简化处理，显示文件名
                pageContent = "<h1>File: ${content.fileName}</h1><p>This is a placeholder for file content.</p>"
                state.pageTitle = content.fileName
                state.lastLoadedUrl = "file://${content.fileName}"
            }
            else -> {
                pageContent = "<h1>Welcome to Compose WebView</h1><p>This is a pure Kotlin implementation for OHOS.</p>"
                state.pageTitle = "Welcome"
                state.lastLoadedUrl = "about:blank"
            }
        }
    }

    Box(modifier = modifier) {
        if (isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                CircularProgressIndicator()
                Text(
                    text = "Loading...",
                    modifier = Modifier.padding(top = 16.dp),
                    fontSize = 16.sp
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                // 显示页面标题
                state.pageTitle?.let {
                    Text(
                        text = it,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // 显示URL
                state.lastLoadedUrl?.let {
                    Text(
                        text = it,
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // 显示页面内容
                Text(
                    text = pageContent,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp
                )
            }
        }
    }
}

// 模拟加载URL的函数
private suspend fun loadUrl(
    url: String,
    state: WebViewState,
    navigator: WebViewNavigator
) {
    state.loadingState = LoadingState.Loading(0f)
    state.lastLoadedUrl = url

    // 模拟加载过程
    delay(1000)

    // 模拟页面内容
    val content = when {
        url.contains("google.com") -> {
            "<h1>Google</h1><p>Welcome to Google search page.</p>"
        }
        url.contains("github.com") -> {
            "<h1>GitHub</h1><p>Welcome to GitHub repository hosting service.</p>"
        }
        url.startsWith("https://") -> {
            "<h1>Secure Website</h1><p>You are visiting: $url</p>"
        }
        url.startsWith("http://") -> {
            "<h1>Website</h1><p>You are visiting: $url</p>"
        }
        else -> {
            "<h1>Unknown URL</h1><p>Cannot load: $url</p>"
        }
    }

    state.pageTitle = url.replace(Regex("^https?://(www\\.)?"), "").takeWhile { it != '/' }
    state.loadingState = LoadingState.Finished
}

/**
 * Pure Kotlin implementation of OHOS WebView functionality
 */
class PureKotlinWebView(
    private val state: WebViewState,
    private val navigator: WebViewNavigator
) {
    fun loadUrl(url: String) {
        KLogger.d { "PureKotlinWebView loading URL: $url" }
        // 在实际实现中，这里会触发重新组合来加载新内容
    }

    fun goBack() {
        KLogger.d { "PureKotlinWebView going back" }
        // 在实际实现中，这里会处理后退逻辑
    }

    fun goForward() {
        KLogger.d { "PureKotlinWebView going forward" }
        // 在实际实现中，这里会处理前进逻辑
    }

    fun reload() {
        KLogger.d { "PureKotlinWebView reloading" }
        // 在实际实现中，这里会重新加载当前页面
    }

    fun stopLoading() {
        KLogger.d { "PureKotlinWebView stopping loading" }
        // 在实际实现中，这里会停止加载过程
    }
}
