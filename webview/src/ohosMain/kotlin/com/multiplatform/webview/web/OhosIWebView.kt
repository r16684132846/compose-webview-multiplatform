package com.multiplatform.webview.web

import com.multiplatform.webview.jsbridge.WebViewJsBridge
import com.multiplatform.webview.util.OhosLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import ohos.agp.components.webengine.WebView
import ohos.app.Context
import ohos.utils.net.Uri
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

/**
 * OHOS平台的WebView实现
 */
class OhosIWebView(private val context: Context) : IWebView {
    override val webViewInstance = WebView(context)

    override val scope: CoroutineScope = MainScope()

    override var webViewJsBridge: WebViewJsBridge? = null

    override fun loadUrl(
        url: String,
        additionalHttpHeaders: Map<String, String>,
    ) {
        OhosLogger.debug("加载URL: $url")
        if (additionalHttpHeaders.isEmpty()) {
            webViewInstance.load(url)
        } else {
            // OHOS WebView可能需要通过其他方式设置headers
            webViewInstance.load(url)
            // 在实际实现中，可能需要通过其他方式处理额外的HTTP头
        }
    }

    override fun loadHtml(
        data: String,
        baseUrl: String?,
        mimeType: String?,
        encoding: String?,
        historyUrl: String?,
    ) {
        OhosLogger.debug("加载HTML数据")
        val actualEncoding = encoding ?: "utf-8"
        val actualMimeType = mimeType ?: "text/html"
        val actualHistoryUrl = historyUrl ?: "about:blank"

        webViewInstance.loadHtml(data, baseUrl, actualMimeType, actualEncoding, actualHistoryUrl)
    }

    override fun loadHtmlFile(fileName: String) {
        OhosLogger.debug("加载HTML文件: $fileName")
        // 在OHOS中加载HTML文件，假设文件在assets目录中
        val url = "file:///assets/$fileName"
        webViewInstance.load(url)
    }

    override fun postUrl(
        url: String,
        postData: ByteArray,
    ) {
        OhosLogger.debug("POST请求URL: $url")
        try {
            // 将postData转换为字符串，通常是以UTF-8编码的表单数据
            val postDataString = String(postData, Charsets.UTF_8)

            // 使用WebView的postUrl方法
            webViewInstance.postUrl(url, postDataString)
        } catch (e: UnsupportedEncodingException) {
            // 处理编码异常
            OhosLogger.error("POST请求编码错误: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun handleNavigationEvents() {
        OhosLogger.debug("处理导航事件")
        // 实现OHOS平台导航事件处理逻辑
        // 注册WebView的加载状态回调
        webViewInstance.webAgent = object : ohos.agp.components.webengine.WebAgent() {
            override fun onPageStarted(webView: ohos.agp.components.webengine.WebView) {
                super.onPageStarted(webView)
                // 页面开始加载
                webView.postTask {
                    // 更新加载状态为加载中
                    OhosLogger.debug("页面开始加载")
                }
            }

            override fun onPageFinished(webView: ohos.agp.components.webengine.WebView) {
                super.onPageFinished(webView)
                // 页面加载完成
                webView.postTask {
                    // 更新加载状态为完成
                    OhosLogger.debug("页面加载完成")
                }
            }

            override fun onReceivedError(
                webView: ohos.agp.components.webengine.WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                super.onReceivedError(webView, errorCode, description, failingUrl)
                // 处理加载错误
                webView.postTask {
                    // 记录错误信息
                    OhosLogger.error("页面加载错误: $description, URL: $failingUrl")
                }
            }
        }

        // 设置前进后退状态监听
        webViewInstance.browserAgent = object : ohos.agp.components.webengine.BrowserAgent() {
            override fun onNavigationStateChange(canGoBack: Boolean, canGoForward: Boolean) {
                super.onNavigationStateChange(canGoBack, canGoForward)
                // 更新导航状态
                webViewInstance.postTask {
                    // 更新导航状态
                    OhosLogger.debug("导航状态变化: canGoBack=$canGoBack, canGoForward=$canGoForward")
                }
            }
        }
    }

    override fun injectJsBridge() {
        OhosLogger.debug("注入JavaScript桥接")
        // 实现OHOS平台JavaScript桥接逻辑
        if (webViewJsBridge == null) return

        val jsBridgeName = webViewJsBridge!!.jsBridgeName
        val initJs = """
            window.$jsBridgeName = {
                callbacks: {},
                callbackId: 0,
                callNative: function (methodName, params, callback) {
                    var message = {
                        methodName: methodName,
                        params: params,
                        callbackId: callback ? window.$jsBridgeName.callbackId++ : -1
                    };
                    if (callback) {
                        window.$jsBridgeName.callbacks[message.callbackId] = callback;
                        console.log('add callback: ' + message.callbackId + ', ' + callback);
                    }
                    window.$jsBridgeName.postMessage(JSON.stringify(message));
                },
                onCallback: function (callbackId, data) {
                    var callback = window.$jsBridgeName.callbacks[callbackId];
                    console.log('onCallback: ' + callbackId + ', ' + data + ', ' + callback);
                    if (callback) {
                        callback(data);
                        delete window.$jsBridgeName.callbacks[callbackId];
                    }
                }
            };
        """.trimIndent()

        evaluateJavascript("javascript:$initJs", null)
    }

    override fun stopLoading() {
        OhosLogger.debug("停止加载")
        webViewInstance.stopLoading()
    }

    override fun evaluateJavascript(
        script: String,
        callback: ((String?) -> Unit)?,
    ) {
        OhosLogger.debug("执行JavaScript: $script")
        // OHOS中执行JavaScript
        webViewInstance.runJavaScript(script) { result ->
            callback?.invoke(result)
        }
    }

    override fun reload() {
        OhosLogger.debug("重新加载")
        webViewInstance.reload()
    }

    override fun destroy() {
        OhosLogger.debug("销毁WebView")
        webViewInstance.destroy()
    }

    override fun canGoBack(): Boolean = webViewInstance.canGoBack()

    override fun canGoForward(): Boolean = webViewInstance.canGoForward()

    override fun goBack() {
        OhosLogger.debug("返回上一页")
        webViewInstance.goBack()
    }

    override fun goForward() {
        OhosLogger.debug("前进到下一页")
        webViewInstance.goForward()
    }

    override fun evaluateJavaScript(
        script: String,
        callback: ((String) -> Unit)?
    ) {
        OhosLogger.debug("执行JavaScript (evaluateJavaScript): $script")
        webViewInstance.evaluateJavascript(script) { result ->
            callback?.invoke(result ?: "")
        }
    }

    override fun initJsBridge(webViewJsBridge: WebViewJsBridge) {
        OhosLogger.debug("初始化JavaScript桥接")
        this.webViewJsBridge = webViewJsBridge
        injectJsBridge()
    }

    override fun saveState(): WebViewBundle? {
        OhosLogger.debug("保存状态")
        // OHOS平台保存状态的实现
        // 根据OHOS API文档，应该使用适当的API来保存WebView状态
        // 这里提供一个基础实现，实际项目中可能需要根据具体需求进行调整
        return try {
            // 创建一个简单的状态保存实现
            val bundle = ohos.utils.PacMap()
            // 注意：这只是一个示例实现，实际的状态保存可能需要更多细节
            bundle.putStringValue("last_url", webViewInstance.url ?: "")
            bundle
        } catch (e: Exception) {
            OhosLogger.error("保存状态失败: ${e.message}")
            null
        }
    }

    override fun scrollOffset(): Pair<Int, Int> {
        OhosLogger.debug("获取滚动偏移量")
        // 返回WebView的滚动偏移量
        return try {
            val x = webViewInstance.scrollX
            val y = webViewInstance.scrollY
            Pair(x, y)
        } catch (e: Exception) {
            OhosLogger.error("获取滚动偏移量失败: ${e.message}")
            Pair(0, 0)
        }
    }
}
