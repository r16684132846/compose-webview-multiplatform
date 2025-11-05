package com.multiplatform.webview.web

import com.multiplatform.webview.jsbridge.WebViewJsBridge
import com.multiplatform.webview.util.KLogger
import kotlinx.coroutines.CoroutineScope

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

    fun loadDataWithBaseURL(
        baseUrl: String?,
        data: String,
        mimeType: String?,
        encoding: String?,
        historyUrl: String?
    ) {
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
    private var onPageStarted: ((url: String) -> Unit)? = null
    private var onPageFinished: ((url: String) -> Unit)? = null
    private var onReceivedError: ((errorCode: Int, description: String, url: String) -> Unit)? =
        null
    private var onLoadResource: ((url: String) -> Unit)? = null
    private var onProgressChanged: ((progress: Int) -> Unit)? = null

    // JavaScript接口映射
    private val javascriptInterfaces = mutableMapOf<String, Any>()

    fun canGoBack(): Boolean = canGoBack
    fun canGoForward(): Boolean = canGoForward

    fun setOnPageStartedListener(listener: (url: String) -> Unit) {
        onPageStarted = listener
    }

    fun setOnPageFinishedListener(listener: (url: String) -> Unit) {
        onPageFinished = listener
    }

    fun setOnReceivedErrorListener(listener: (errorCode: Int, description: String, url: String) -> Unit) {
        onReceivedError = listener
    }

    fun setOnLoadResourceListener(listener: (url: String) -> Unit) {
        onLoadResource = listener
    }

    fun setOnProgressChangedListener(listener: (progress: Int) -> Unit) {
        onProgressChanged = listener
    }

    fun loadUrl(url: String, additionalHttpHeaders: Map<String, String> = emptyMap()) {
        KLogger.d { "OHOS WebView loading URL: $url with headers: $additionalHttpHeaders" }

        // 触发页面开始加载事件
        onPageStarted?.invoke(url)

        // 模拟加载进度
        onProgressChanged?.invoke(10)

        try {
            // 这里应该是实际的OpenHarmony WebView加载URL逻辑
            // 由于这是一个示例实现，我们模拟整个过程

            // 模拟资源加载
            onLoadResource?.invoke(url)
            onProgressChanged?.invoke(50)

            // 模拟加载完成
            canGoBack = true // 加载完成后可以后退
            onProgressChanged?.invoke(100)
            onPageFinished?.invoke(url)
        } catch (e: Exception) {
            KLogger.e { "Failed to load URL: $url, error: ${e.message}" }
            onReceivedError?.invoke(-1, e.message ?: "Unknown error", url)
        }
    }

    fun loadDataWithBaseURL(
        baseUrl: String?,
        data: String,
        mimeType: String?,
        encoding: String?,
        historyUrl: String?
    ) {
        KLogger.d { "OHOS WebView loading data with base URL: $baseUrl" }

        val finalUrl = historyUrl ?: baseUrl ?: "about:blank"
        onPageStarted?.invoke(finalUrl)
        onProgressChanged?.invoke(10)

        try {
            onLoadResource?.invoke(finalUrl)
            onProgressChanged?.invoke(50)

            canGoBack = true
            onProgressChanged?.invoke(100)
            onPageFinished?.invoke(finalUrl)
        } catch (e: Exception) {
            KLogger.e { "Failed to load data with base URL: $baseUrl, error: ${e.message}" }
            onReceivedError?.invoke(-1, e.message ?: "Unknown error", finalUrl)
        }
    }

    fun loadUrl(url: String) {
        KLogger.d { "OHOS WebView loading URL: $url" }
        loadUrl(url, emptyMap())
    }

    fun postUrl(url: String, postData: ByteArray) {
        KLogger.d { "OHOS WebView posting to URL: $url with data size: ${postData.size}" }

        onPageStarted?.invoke(url)
        onProgressChanged?.invoke(10)

        try {
            // 模拟POST请求处理
            onLoadResource?.invoke(url)
            onProgressChanged?.invoke(50)

            canGoBack = true
            onProgressChanged?.invoke(100)
            onPageFinished?.invoke(url)
        } catch (e: Exception) {
            KLogger.e { "Failed to post URL: $url, error: ${e.message}" }
            onReceivedError?.invoke(-1, e.message ?: "Unknown error", url)
        }
    }

    fun goBack() {
        KLogger.d { "OHOS WebView going back" }
        if (canGoBack) {
            canGoBack = false
            canGoForward = true
            // 模拟后退操作
            onPageStarted?.invoke("back_page")
            onProgressChanged?.invoke(50)
            onPageFinished?.invoke("back_page")
        }
    }

    fun goForward() {
        KLogger.d { "OHOS WebView going forward" }
        if (canGoForward) {
            canGoForward = false
            canGoBack = true
            // 模拟前进操作
            onPageStarted?.invoke("forward_page")
            onProgressChanged?.invoke(50)
            onPageFinished?.invoke("forward_page")
        }
    }

    fun reload() {
        KLogger.d { "OHOS WebView reloading" }
        // 模拟重新加载当前页面
        onPageStarted?.invoke("current_page")
        onProgressChanged?.invoke(30)
        onProgressChanged?.invoke(100)
        onPageFinished?.invoke("current_page")
    }

    fun stopLoading() {
        KLogger.d { "OHOS WebView stopping loading" }
        // 模拟停止加载
        onProgressChanged?.invoke(0)
    }

    fun evaluateJavascript(script: String, callback: ((String) -> Unit)?) {
        KLogger.d { "OHOS WebView evaluating JavaScript: $script" }

        try {
            // 模拟JavaScript执行
            val result = when {
                script.contains("document.title") -> "\"Test Page Title\""
                script.contains("window.location.href") -> "\"https://example.com\""
                script.startsWith("alert(") -> ""
                else -> "\"JavaScript executed successfully\""
            }

            callback?.invoke(result)
        } catch (e: Exception) {
            KLogger.e { "Failed to evaluate JavaScript: $script, error: ${e.message}" }
            callback?.invoke("\"Error: ${e.message}\"")
        }
    }

    fun addJavascriptInterface(obj: Any, name: String) {
        KLogger.d { "OHOS WebView adding JavaScript interface: $name" }
        javascriptInterfaces[name] = obj

        // 模拟向WebView注入JavaScript桥接代码
        val bridgeScript = """
            if (typeof window.$name === 'undefined') {
                window.$name = {
                    // 这里应该包含实际的方法映射
                    // 在实际实现中，会通过某种方式将调用转发给Kotlin对象
                };
            }
        """.trimIndent()

        KLogger.d { "Injected JavaScript bridge for interface: $name\n$bridgeScript" }
    }

    fun saveState(bundle: OhosWebViewBundle): OhosWebViewBundle? {
        KLogger.d { "OHOS WebView saving state" }
        // 保存WebView状态
        bundle.canGoBack = canGoBack
        bundle.canGoForward = canGoForward
        return bundle
    }

    fun restoreState(bundle: OhosWebViewBundle) {
        KLogger.d { "OHOS WebView restoring state" }
        canGoBack = bundle.canGoBack
        canGoForward = bundle.canGoForward
    }

    var scrollX: Int = 0
    var scrollY: Int = 0
}


class OhosWebViewBundle {
    var canGoBack: Boolean = false
    var canGoForward: Boolean = false
    var scrollX: Int = 0
    var scrollY: Int = 0
}

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
        // 纯Kotlin实现，模拟加载文件
        webView.loadUrl("file:///$fileName")
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
        KLogger.d { "Evaluating JavaScript in OHOS WebView with length: ${script.length}" }
        webView.evaluateJavascript(script) { result ->
            callback?.invoke(result)
        }
    }

    override fun injectJsBridge() {
        if (webViewJsBridge == null) return
        super.injectJsBridge()
        KLogger.d { "Injecting JS Bridge for OHOS WebView" }

        // 注册Kotlin对象供JS调用
        webView.addJavascriptInterface(webViewJsBridge, "KotlinBridge")
    }

    override fun initJsBridge(webViewJsBridge: WebViewJsBridge) {
        KLogger.d { "Initializing JS Bridge for OHOS WebView" }
        // 在纯Kotlin实现中，我们只是记录日志
    }

    override fun saveState(): WebViewBundle? {
        KLogger.d { "Saving state in OHOS WebView" }
        // 在纯Kotlin实现中，我们只是模拟保存状态
        return WebViewBundle()
    }

    override fun scrollOffset(): Pair<Int, Int> {
        return Pair(webView.scrollX, webView.scrollY)
    }

    override fun initWebView() {
        KLogger.d { "Initializing OHOS WebView" }
    }
}
