package com.multiplatform.webview.web

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.multiplatform.webview.jsbridge.WebViewJsBridge
import com.multiplatform.webview.util.KLogger
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import ohos.agp.components.webengine.WebView

/**
 * OHOS WebView implementation
 */
@Composable
internal fun OhosWebView(
    state: WebViewState,
    modifier: Modifier = Modifier,
    captureBackPresses: Boolean = true,
    navigator: WebViewNavigator = rememberWebViewNavigator(),
    webViewJsBridge: WebViewJsBridge? = null,
    onCreated: (NativeWebView) -> Unit = {},
    onDispose: (NativeWebView) -> Unit = {},
    factory: (WebViewFactoryParam) -> NativeWebView,
) {
    val coroutineScope = rememberCoroutineScope()
    var webView by remember { mutableStateOf<NativeWebView?>(null) }

    // 创建OHOS WebView组件
    val context = LocalContext.current
    val ohosWebView = remember {
        factory(WebViewFactoryParam(context)).also { webViewInstance ->
            webView = webViewInstance
            onCreated(webViewInstance)
            webViewJsBridge?.webView = webViewInstance as? IWebView
        }
    }

    // 处理导航事件
    webView?.let { wv ->
        LaunchedEffect(wv, navigator) {
            with(navigator) {
                KLogger.d {
                    "wv.handleNavigationEvents()"
                }
                wv.handleNavigationEvents()
            }
        }

        LaunchedEffect(wv, state) {
            snapshotFlow { state.content }.collect { content ->
                when (content) {
                    is WebContent.Url -> {
                        state.lastLoadedUrl = content.url
                        wv.loadUrl(content.url, content.additionalHttpHeaders)
                    }

                    is WebContent.Data -> {
                        wv.loadHtml(
                            content.data,
                            content.baseUrl,
                            content.mimeType,
                            content.encoding,
                            content.historyUrl,
                        )
                    }

                    is WebContent.File -> {
                        wv.loadHtmlFile(content.fileName)
                    }

                    is WebContent.Post -> {
                        wv.postUrl(
                            content.url,
                            content.postData,
                        )
                    }

                    is WebContent.NavigatorOnly -> {
                        // NO-OP
                    }
                }
            }
        }

        // inject the js bridge when the webview is loaded.
        if (webViewJsBridge != null) {
            LaunchedEffect(wv, state) {
                val loadingStateFlow =
                    snapshotFlow { state.loadingState }.filter { it is LoadingState.Finished }
                val lastLoadedUrFlow =
                    snapshotFlow { state.lastLoadedUrl }.filter { !it.isNullOrEmpty() }

                // Only inject the js bridge when url is changed and the loading state is finished.
                merge(loadingStateFlow, lastLoadedUrFlow).collect {
                    // double check the loading state to make sure the WebView is loaded.
                    if (state.loadingState is LoadingState.Finished) {
                        wv.injectJsBridge()
                    }
                }
            }
        }
    }

    // 实现OHOS WebView UI组件
    Box(modifier = modifier) {
        AndroidView(
            factory = { context ->
                (ohosWebView as OhosIWebView).webViewInstance
            }
        )
    }

    // 在WebView销毁时调用onDispose回调
    DisposableEffect(webView) {
        onDispose {
            webView?.let {
                it.destroy()
                onDispose(it)
            }
        }
    }
}
