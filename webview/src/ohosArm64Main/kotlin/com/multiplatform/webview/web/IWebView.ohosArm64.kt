package com.multiplatform.webview.web

import com.multiplatform.webview.jsbridge.JsMessage
import com.multiplatform.webview.jsbridge.WebViewJsBridge
import com.multiplatform.webview.util.KLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.Json

/**
 * Created By Kevin Zou On 2023/9/5
 */

// OHOS原生WebView类型
actual typealias NativeWebView = OhosWebView

// OHOS原生WebView包装器
class OhosWebView {
    private var webViewClient: OhosWebViewClient? = null

    fun canGoBack(): Boolean = webViewClient?.canGoBack() ?: false
    fun canGoForward(): Boolean = webViewClient?.canGoForward() ?: false

    fun loadUrl(url: String, additionalHttpHeaders: Map<String, String> = emptyMap()) {
        webViewClient?.loadUrl(url, additionalHttpHeaders)
    }

    fun loadDataWithBaseURL(baseUrl: String?, data: String, mimeType: String?, encoding: String?, historyUrl: String?) {
        webViewClient?.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl)
    }

    fun loadUrl(url: String) {
        webViewClient?.loadUrl(url)
    }

    fun postUrl(url: String, postData: ByteArray) {
        webViewClient?.postUrl(url, postData)
    }

    fun goBack() {
        webViewClient?.goBack()
    }

    fun goForward() {
        webViewClient?.goForward()
    }

    fun reload() {
        webViewClient?.reload()
    }

    fun stopLoading() {
        webViewClient?.stopLoading()
    }

    fun evaluateJavascript(script: String, callback: ((String) -> Unit)?) {
        webViewClient?.evaluateJavascript(script, callback)
    }

    fun addJavascriptInterface(obj: Any, name: String) {
        webViewClient?.addJavascriptInterface(obj, name)
    }

    fun saveState(bundle: OhosWebViewBundle): OhosWebViewBundle? {
        return webViewClient?.saveState(bundle)
    }

    var scrollX: Int = 0
        get() = webViewClient?.scrollX ?: 0
    var scrollY: Int = 0
        get() = webViewClient?.scrollY ?: 0

    fun setWebViewClient(client: OhosWebViewClient) {
        this.webViewClient = client
    }
}

// OHOS WebView客户端
class OhosWebViewClient {
    private var canGoBack: Boolean = false
    private var canGoForward: Boolean = false

    fun canGoBack(): Boolean = canGoBack
    fun canGoForward(): Boolean = canGoForward

    fun loadUrl(url: String, additionalHttpHeaders: Map<String, String> = emptyMap()) {
        // 实际的OHOS WebView加载URL逻辑
        KLogger.d { "OHOS WebView loading URL: $url with headers: $additionalHttpHeaders" }
    }

    fun loadDataWithBaseURL(baseUrl: String?, data: String, mimeType: String?, encoding: String?, historyUrl: String?) {
        // 实际的OHOS WebView加载数据逻辑
        KLogger.d { "OHOS WebView loading data with base URL: $baseUrl" }
    }

    fun loadUrl(url: String) {
        // 实际的OHOS WebView加载URL逻辑
        KLogger.d { "OHOS WebView loading URL: $url" }
    }

    fun postUrl(url: String, postData: ByteArray) {
        // 实际的OHOS WebView POST请求逻辑
        KLogger.d { "OHOS WebView posting to URL: $url with data size: ${postData.size}" }
    }

    fun goBack() {
        // 实际的OHOS WebView后退逻辑
        KLogger.d { "OHOS WebView going back" }
    }

    fun goForward() {
        // 实际的OHOS WebView前进逻辑
        KLogger.d { "OHOS WebView going forward" }
    }

    fun reload() {
        // 实际的OHOS WebView重新加载逻辑
        KLogger.d { "OHOS WebView reloading" }
    }

    fun stopLoading() {
        // 实际的OHOS WebView停止加载逻辑
        KLogger.d { "OHOS WebView stopping loading" }
    }

    fun evaluateJavascript(script: String, callback: ((String) -> Unit)?) {
        // 实际的OHOS WebView执行JavaScript逻辑
        KLogger.d { "OHOS WebView evaluating JavaScript: $script" }
        callback?.invoke("") // 回调空结果，实际应该返回执行结果
    }

    fun addJavascriptInterface(obj: Any, name: String) {
        // 实际的OHOS WebView添加JavaScript接口逻辑
        KLogger.d { "OHOS WebView adding JavaScript interface: $name" }
    }

    fun saveState(bundle: OhosWebViewBundle): OhosWebViewBundle? {
        // 实际的OHOS WebView保存状态逻辑
        KLogger.d { "OHOS WebView saving state" }
        return bundle
    }

    var scrollX: Int = 0
    var scrollY: Int = 0
}

// OHOS WebView Bundle
class OhosWebViewBundle

/**
 * OHOS implementation of [IWebView]
 */
class OHOSWebView(
    override val webView: OhosWebView,
    override val scope: CoroutineScope,
    override val webViewJsBridge: WebViewJsBridge?
) : IWebView {
    init {
        // 初始化WebView客户端
        webView.setWebViewClient(OhosWebViewClient())
        initWebView()
    }

    override fun canGoBack(): Boolean {
        KLogger.d { "Checking if OHOS WebView can go back" }
        return webView.canGoBack()
    }

    override fun canGoForward(): Boolean {
        KLogger.d { "Checking if OHOS WebView can go forward" }
        return webView.canGoForward()
    }

    override fun loadUrl(
        url: String,
        additionalHttpHeaders: Map<String, String>
    ) {
        KLogger.d { "Loading URL in OHOS WebView: $url" }
        if (additionalHttpHeaders.isNotEmpty()) {
            KLogger.d { "With additional HTTP headers: ${additionalHttpHeaders.size}" }
        }
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
        KLogger.d { "Loading HTML content in OHOS WebView with length: ${html.length}" }
        webView.loadDataWithBaseURL(baseUrl, html, mimeType, encoding, historyUrl)
    }

    override suspend fun loadHtmlFile(fileName: String) {
        KLogger.d { "Loading HTML file in OHOS WebView: $fileName" }
        webView.loadUrl("file:///android_asset/$fileName")
    }

    override fun postUrl(url: String, postData: ByteArray) {
        KLogger.d { "Posting to URL in OHOS WebView: $url with data size: ${postData.size}" }
        webView.postUrl(url, postData)
    }

    override fun goBack() {
        KLogger.d { "Going back in OHOS WebView" }
        webView.goBack()
    }

    override fun goForward() {
        KLogger.d { "Going forward in OHOS WebView" }
        webView.goForward()
    }

    override fun reload() {
        KLogger.d { "Reloading OHOS WebView" }
        webView.reload()
    }

    override fun stopLoading() {
        KLogger.d { "Stopping OHOS WebView loading" }
        webView.stopLoading()
    }

    override fun evaluateJavaScript(script: String, callback: ((String) -> Unit)?) {
        val ohosScript = "javascript:$script"
        KLogger.d { "Evaluating JavaScript in OHOS WebView with length: ${script.length}" }
        webView.evaluateJavascript(ohosScript, callback)
    }

    override fun injectJsBridge() {
        if (webViewJsBridge == null) return
        super.injectJsBridge()
        val callOHOS =
            """
            window.${webViewJsBridge.jsBridgeName}.postMessage = function (message) {
                    window.ohosJsBridge.call(message);
                };
            """.trimIndent()
        evaluateJavaScript(callOHOS)
    }

    override fun initJsBridge(webViewJsBridge: WebViewJsBridge) {
        KLogger.d { "Initializing JS Bridge for OHOS WebView" }
        webView.addJavascriptInterface(this, "ohosJsBridge")
    }

    // JS桥接方法
    fun call(request: String) {
        KLogger.d { "call from JS in OHOS WebView: $request" }
        val message = Json.decodeFromString<JsMessage>(request)
        KLogger.d { "call from JS in OHOS WebView: $message" }
        webViewJsBridge?.dispatch(message)
    }

    fun callOHOS(
        id: Int,
        method: String,
        params: String,
    ) {
        KLogger.d { "callOHOS call from JS in OHOS WebView: $id, $method, $params" }
        webViewJsBridge?.dispatch(JsMessage(id, method, params))
    }

    override fun saveState(): WebViewBundle? {
        val bundle = OhosWebViewBundle()
        val result = webView.saveState(bundle)
        return if (result != null) {
            WebViewBundle()
        } else {
            null
        }
    }

    override fun scrollOffset(): Pair<Int, Int> {
        return Pair(webView.scrollX, webView.scrollY)
    }

    override fun initWebView() {
        KLogger.d { "Initializing OHOS WebView" }
    }
}
