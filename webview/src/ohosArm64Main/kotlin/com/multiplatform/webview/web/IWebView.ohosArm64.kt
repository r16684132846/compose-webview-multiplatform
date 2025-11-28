package com.multiplatform.webview.web

import com.multiplatform.webview.jsbridge.WebViewJsBridge
import com.multiplatform.webview.util.KLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.cinterop.*
import kotlin.experimental.ExperimentalNativeApi

// C API函数声明
@OptIn(ExperimentalNativeApi::class)
@ExperimentalForeignApi
@CName("OH_WebViewManager_GetInstance")
external fun OH_WebViewManager_GetInstance(): COpaquePointer?

@OptIn(ExperimentalNativeApi::class)
@ExperimentalForeignApi
@CName("OH_WebViewManager_Init")
external fun OH_WebViewManager_Init(manager: COpaquePointer?)

@OptIn(ExperimentalNativeApi::class)
@ExperimentalForeignApi
@CName("OH_WebView_Create")
external fun OH_WebView_Create(context: COpaquePointer?, manager: COpaquePointer?): COpaquePointer?

@OptIn(ExperimentalNativeApi::class)
@ExperimentalForeignApi
@CName("OH_WebView_Destroy")
external fun OH_WebView_Destroy(webview: COpaquePointer?)

@OptIn(ExperimentalNativeApi::class)
@ExperimentalForeignApi
@CName("OH_WebView_LoadUrl")
external fun OH_WebView_LoadUrl(webview: COpaquePointer?, url: String)

// 新增函数声明
@OptIn(ExperimentalNativeApi::class)
@ExperimentalForeignApi
@CName("OH_SetAbilityContext")
external fun OH_SetAbilityContext(context: COpaquePointer?)


@OptIn(ExperimentalNativeApi::class)
@ExperimentalForeignApi
@CName("OH_WebView_LoadData")
external fun OH_WebView_LoadData(
    webview: COpaquePointer?,
    data: String,
    mimeType: String?,
    encoding: String?,
    baseUrl: String?
)

@OptIn(ExperimentalNativeApi::class)
@ExperimentalForeignApi
@CName("OH_WebView_EvaluateJavascript")
external fun OH_WebView_EvaluateJavascript(
    webview: COpaquePointer?,
    script: String,
    callback: COpaquePointer?,
    userData: COpaquePointer?
)

@OptIn(ExperimentalNativeApi::class)
@ExperimentalForeignApi
@CName("OH_WebView_CanGoBack")
external fun OH_WebView_CanGoBack(webview: COpaquePointer?): Boolean

@OptIn(ExperimentalNativeApi::class)
@ExperimentalForeignApi
@CName("OH_WebView_CanGoForward")
external fun OH_WebView_CanGoForward(webview: COpaquePointer?): Boolean

@OptIn(ExperimentalNativeApi::class)
@ExperimentalForeignApi
@CName("OH_WebView_GoBack")
external fun OH_WebView_GoBack(webview: COpaquePointer?)

@OptIn(ExperimentalNativeApi::class)
@ExperimentalForeignApi
@CName("OH_WebView_GoForward")
external fun OH_WebView_GoForward(webview: COpaquePointer?)

@OptIn(ExperimentalNativeApi::class)
@ExperimentalForeignApi
@CName("OH_WebView_Reload")
external fun OH_WebView_Reload(webview: COpaquePointer?)

@OptIn(ExperimentalNativeApi::class)
@ExperimentalForeignApi
@CName("OH_WebView_StopLoading")
external fun OH_WebView_StopLoading(webview: COpaquePointer?)

@OptIn(ExperimentalNativeApi::class)
@ExperimentalForeignApi
@CName("OH_WebView_SetPageStartedCallback")
external fun OH_WebView_SetPageStartedCallback(
    webview: COpaquePointer?,
    callback: COpaquePointer?,
    userData: COpaquePointer?
)

@OptIn(ExperimentalNativeApi::class)
@ExperimentalForeignApi
@CName("OH_WebView_AddJavascriptInterface")
external fun OH_WebView_AddJavascriptInterface(
    webview: COpaquePointer?,
    name: String,
    obj: COpaquePointer?
)

@OptIn(ExperimentalNativeApi::class)
@ExperimentalForeignApi
@CName("OH_WebView_SetPageFinishedCallback")
external fun OH_WebView_SetPageFinishedCallback(
    webview: COpaquePointer?,
    callback: COpaquePointer?,
    userData: COpaquePointer?
)

@OptIn(ExperimentalNativeApi::class)
@ExperimentalForeignApi
@CName("OH_WebView_SetErrorCallback")
external fun OH_WebView_SetErrorCallback(
    webview: COpaquePointer?,
    callback: COpaquePointer?,
    userData: COpaquePointer?
)

/**
 * Created By Kevin Zou On 2023/9/5
 */

// OHOS原生WebView类型
actual typealias NativeWebView = OhosWebView

// JavaScript执行回调数据结构
@ExperimentalForeignApi
class JsCallbackData(
    val callback: ((String) -> Unit)?,
    val user_data: COpaquePointer?
)

// 页面开始加载回调
@OptIn(ExperimentalNativeApi::class)
@ExperimentalForeignApi
@CName("page_started_callback")
fun pageStartedCallback(url: CPointer<ByteVar>?, userData: COpaquePointer?) {
    // 实际实现中处理页面开始加载事件
    KLogger.d { "Page started: ${url?.toKString()}" }
}

// 页面加载完成回调
@OptIn(ExperimentalNativeApi::class)
@ExperimentalForeignApi
@CName("page_finished_callback")
fun pageFinishedCallback(url: CPointer<ByteVar>?, userData: COpaquePointer?) {
    // 实际实现中处理页面加载完成事件
    KLogger.d { "Page finished: ${url?.toKString()}" }
}

// 错误回调
@OptIn(ExperimentalNativeApi::class)
@ExperimentalForeignApi
@CName("error_callback")
fun errorCallback(
    errorCode: Int,
    description: CPointer<ByteVar>?,
    failingUrl: CPointer<ByteVar>?,
    userData: COpaquePointer?
) {
    // 实际实现中处理错误事件
    KLogger.d { "Error occurred: code=$errorCode, description=${description?.toKString()}, url=${failingUrl?.toKString()}" }
}

// JavaScript执行回调
@OptIn(ExperimentalNativeApi::class)
@ExperimentalForeignApi
@CName("js_execute_callback")
fun jsExecuteCallback(result: CPointer<ByteVar>?, userData: COpaquePointer?) {
    // 实际实现中处理JavaScript执行结果
    KLogger.d { "JavaScript executed, result: ${result?.toKString()}" }
}

// OHOS原生WebView包装器
class OhosWebView {
    @OptIn(ExperimentalForeignApi::class)
    private var nativeWebView: COpaquePointer? = null
    private var webViewWrapper: OhosWebViewWrapper? = null

    @OptIn(ExperimentalForeignApi::class)
    private var manager: COpaquePointer? = null

    // 添加上下文变量
    @OptIn(ExperimentalForeignApi::class)
    private var context: COpaquePointer? = null

    init {
        // 初始化WebView管理器
        @OptIn(ExperimentalForeignApi::class)
        manager = OH_WebViewManager_GetInstance()
        @OptIn(ExperimentalForeignApi::class)
        if (manager != null) {
            OH_WebViewManager_Init(manager)
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    fun setContext(context: COpaquePointer?) {
        this.context = context
        // 设置Ability上下文
        OH_SetAbilityContext(context)
    }

    @OptIn(ExperimentalForeignApi::class)
    fun canGoBack(): Boolean {
        return nativeWebView?.let {
            OH_WebView_CanGoBack(it)
        } ?: false
    }

    @OptIn(ExperimentalForeignApi::class)
    fun canGoForward(): Boolean {
        return nativeWebView?.let {
            OH_WebView_CanGoForward(it)
        } ?: false
    }

    @OptIn(ExperimentalForeignApi::class)
    fun loadUrl(url: String, additionalHttpHeaders: Map<String, String> = emptyMap()) {
        if (nativeWebView == null && manager != null) {
            // 创建WebView实例（这里需要传入正确的上下文）
            nativeWebView = OH_WebView_Create(null, manager)
        }

        nativeWebView?.let { webView ->
            OH_WebView_LoadUrl(webView, url)
            KLogger.d { "Loading URL: $url" }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    fun loadDataWithBaseURL(
        baseUrl: String?,
        data: String,
        mimeType: String?,
        encoding: String?,
        historyUrl: String?
    ) {
        if (nativeWebView == null && manager != null) {
            // 创建WebView实例（这里需要传入正确的上下文）
            nativeWebView = OH_WebView_Create(null, manager)
        }

        nativeWebView?.let { webView ->
            OH_WebView_LoadData(webView, data, mimeType, encoding, baseUrl)
            KLogger.d { "Loading data with base URL: $baseUrl" }
        }
    }

    fun loadUrl(url: String) {
        loadUrl(url, emptyMap())
    }

    fun postUrl(url: String, postData: ByteArray) {
        // 在实际实现中，需要通过其他方式处理POST请求
        KLogger.d { "Posting to URL: $url with data size: ${postData.size}" }
        loadUrl(url)
    }

    @OptIn(ExperimentalForeignApi::class)
    fun goBack() {
        nativeWebView?.let {
            OH_WebView_GoBack(it)
            KLogger.d { "Going back" }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    fun goForward() {
        nativeWebView?.let {
            OH_WebView_GoForward(it)
            KLogger.d { "Going forward" }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    fun reload() {
        nativeWebView?.let {
            OH_WebView_Reload(it)
            KLogger.d { "Reloading" }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    fun stopLoading() {
        nativeWebView?.let {
            OH_WebView_StopLoading(it)
            KLogger.d { "Stopping loading" }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    fun evaluateJavascript(script: String, callback: ((String) -> Unit)?) {
        nativeWebView?.let { webView ->
            if (callback != null) {
                // 创建回调数据结构
                // 注意：这需要更复杂的实现来处理回调
                OH_WebView_EvaluateJavascript(webView, script, null, null)
            } else {
                OH_WebView_EvaluateJavascript(webView, script, null, null)
            }
            KLogger.d { "Evaluating JavaScript: $script" }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    fun addJavascriptInterface(obj: Any, name: String) {
        nativeWebView?.let { webView ->
            // 在实际实现中需要正确处理JavaScript接口
            OH_WebView_AddJavascriptInterface(webView, name, null)
            KLogger.d { "Adding JavaScript interface: $name" }
        }
    }

    fun saveState(bundle: OhosWebViewBundle): OhosWebViewBundle? {
        KLogger.d { "Saving state" }
        // 实际实现中需要保存WebView状态
        bundle.canGoBack = canGoBack()
        bundle.canGoForward = canGoForward()
        return bundle
    }

    var scrollX: Int = 0
        get() = 0 // 实际实现中需要获取滚动位置
    var scrollY: Int = 0
        get() = 0 // 实际实现中需要获取滚动位置

    @OptIn(ExperimentalForeignApi::class)
    fun setWebViewClient(client: OhosWebViewClient) {
        // 实际实现中需要设置WebView客户端
        // 这里注册回调函数
        nativeWebView?.let { webView ->
            OH_WebView_SetPageStartedCallback(webView, staticCFunction(::pageStartedCallback), null)
            OH_WebView_SetPageFinishedCallback(
                webView,
                staticCFunction(::pageFinishedCallback),
                null
            )
            OH_WebView_SetErrorCallback(webView, staticCFunction(::errorCallback), null)
        }
    }

    fun setWebViewWrapper(wrapper: OhosWebViewWrapper) {
        this.webViewWrapper = wrapper
    }

    fun getWebViewWrapper(): OhosWebViewWrapper? = this.webViewWrapper
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
        onProgressChanged?.invoke(50)
        onProgressChanged?.invoke(100)

        // 模拟加载完成
        canGoBack = true // 加载完成后可以后退
        onPageFinished?.invoke(url)
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
        onProgressChanged?.invoke(50)
        onProgressChanged?.invoke(100)
        canGoBack = true
        onPageFinished?.invoke(finalUrl)
    }

    fun loadUrl(url: String) {
        KLogger.d { "OHOS WebView loading URL: $url" }
        loadUrl(url, emptyMap())
    }

    fun postUrl(url: String, postData: ByteArray) {
        KLogger.d { "OHOS WebView posting to URL: $url with data size: ${postData.size}" }

        onPageStarted?.invoke(url)
        onProgressChanged?.invoke(10)
        onProgressChanged?.invoke(50)
        onProgressChanged?.invoke(100)
        canGoBack = true
        onPageFinished?.invoke(url)
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
        webView.loadUrl("file://assets/$fileName")
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
        webView.evaluateJavascript(script, callback)
    }

    override fun injectJsBridge() {
        if (webViewJsBridge == null) return
        super.injectJsBridge()
        KLogger.d { "Injecting JS Bridge for OHOS WebView" }

        // 注入桥接代码
        val bridgeScript = """
            window.bridge = {
                onRenderFinished: function() {
                    if (window.KotlinBridge) {
                        window.KotlinBridge.postMessage(JSON.stringify({
                            method: 'onRenderFinished',
                            params: []
                        }));
                    }
                },
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
        KLogger.d { "Initializing JS Bridge for OHOS WebView" }
        webView.addJavascriptInterface(webViewJsBridge, "KotlinBridge")
    }

    override fun saveState(): WebViewBundle? {
        KLogger.d { "Saving state in OHOS WebView" }
        val bundle = OhosWebViewBundle()
        return webView.saveState(bundle) as WebViewBundle?
    }

    override fun scrollOffset(): Pair<Int, Int> {
        return Pair(webView.scrollX, webView.scrollY)
    }

    override fun initWebView() {
        KLogger.d { "Initializing OHOS WebView" }
    }
}
