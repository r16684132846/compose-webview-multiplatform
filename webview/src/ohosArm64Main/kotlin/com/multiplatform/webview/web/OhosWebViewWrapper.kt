package com.multiplatform.webview.web

import com.multiplatform.webview.jsbridge.WebViewJsBridge
import com.multiplatform.webview.util.KLogger
import kotlinx.coroutines.CoroutineScope

/**
 * 鸿蒙WebView的具体实现
 */
class OhosWebViewWrapper(
    override val webView: OhosWebView,
    override val scope: CoroutineScope,
    override val webViewJsBridge: WebViewJsBridge?
) : IWebView {

    private val jsBridge = OhosJsBridge(webView)

    init {
        webView.setWebViewWrapper(this)
        initWebView()
    }

    override fun canGoBack(): Boolean = webView.canGoBack()

    override fun canGoForward(): Boolean = webView.canGoForward()

    override fun loadUrl(
        url: String,
        additionalHttpHeaders: Map<String, String>
    ) {
        KLogger.d { "Loading URL: $url" }
        webView.loadUrl(url, additionalHttpHeaders)
    }

    override fun loadHtml(
        html: String?,
        baseUrl: String?,
        mimeType: String?,
        encoding: String?,
        historyUrl: String?
    ) {
        if (html == null) return
        KLogger.d { "Loading HTML content with ${html.length} characters" }
        webView.loadDataWithBaseURL(
            baseUrl,
            html,
            mimeType ?: "text/html",
            encoding ?: "utf-8",
            historyUrl
        )
    }

    override suspend fun loadHtmlFile(fileName: String) {
        KLogger.d { "Loading HTML file: $fileName" }
        // 实际实现中需要从资源文件加载内容
        webView.loadUrl("file:///$fileName")
    }

    override fun postUrl(url: String, postData: ByteArray) {
        KLogger.d { "Posting to URL: $url" }
        webView.postUrl(url, postData)
    }

    override fun goBack() {
        KLogger.d { "Going back" }
        webView.goBack()
    }

    override fun goForward() {
        KLogger.d { "Going forward" }
        webView.goForward()
    }

    override fun reload() {
        KLogger.d { "Reloading page" }
        webView.reload()
    }

    override fun stopLoading() {
        KLogger.d { "Stopping loading" }
        webView.stopLoading()
    }

    override fun evaluateJavaScript(script: String, callback: ((String) -> Unit)?) {
        KLogger.d { "Evaluating JavaScript: $script" }
        jsBridge.evaluateJavascript(script, callback)
    }

    override fun injectJsBridge() {
        if (webViewJsBridge == null) return
        super.injectJsBridge()
        KLogger.d { "Injecting JS Bridge" }

        // 注入桥接代码，使JS可以调用Kotlin函数
        val bridgeScript = """
            window.bridge = {
                onRenderFinished: function() {
                    // 调用Kotlin中的WebAppInterface.onRenderFinished
                    if (window.KotlinBridge) {
                        window.KotlinBridge.postMessage(JSON.stringify({
                            method: 'onRenderFinished',
                            params: []
                        }));
                    }
                },
                // 可以添加更多方法
                callKotlin: function(methodName, params) {
                    if (window.KotlinBridge) {
                        window.KotlinBridge.postMessage(JSON.stringify({
                            method: methodName,
                            params: params || []
                        }));
                    }
                }
            };
        """.trimIndent()

        evaluateJavaScript(bridgeScript)
    }

    override fun initJsBridge(webViewJsBridge: WebViewJsBridge) {
        KLogger.d { "Initializing JS Bridge" }
        // 注册Kotlin对象供JS调用
        webView.addJavascriptInterface(webViewJsBridge, "KotlinBridge")
    }

    override fun saveState(): WebViewBundle? {
        KLogger.d { "Saving state" }
        // 实现状态保存逻辑
        return WebViewBundle()
    }

    override fun scrollOffset(): Pair<Int, Int> {
        return Pair(0, 0)
    }
}
