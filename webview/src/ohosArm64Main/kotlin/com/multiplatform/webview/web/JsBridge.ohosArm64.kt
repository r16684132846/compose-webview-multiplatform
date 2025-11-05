import com.multiplatform.webview.jsbridge.JsMessage
import com.multiplatform.webview.util.KLogger
import com.multiplatform.webview.web.OhosWebView
import kotlinx.serialization.json.Json

/**
 * 鸿蒙平台的JS桥接实现
 */
class OhosJsBridge(
    private val webView: OhosWebView
) {
    fun postMessage(message: String) {
        KLogger.d { "JS Bridge received message: $message" }

        // 解析JSON消息
        try {
            val json = Json.decodeFromString<JsMessage>(message)
            // 处理消息
            handleJsMessage(json)
        } catch (e: Exception) {
            KLogger.e { "Failed to parse JS message: $message, error: ${e.message}" }
        }
    }

    /**
     * 处理来自JavaScript的消息
     */
    private fun handleJsMessage(message: JsMessage) {
        when (message.methodName) {
            "onRenderFinished" -> {
                // 处理渲染完成事件
                KLogger.d { "JS onRenderFinished called" }
                // 可以在这里添加具体的业务逻辑
            }

            "callKotlin" -> {
                // 处理调用Kotlin方法的请求
                KLogger.d { "JS callKotlin called with params: ${message.params}" }
                // 可以在这里添加具体的业务逻辑
            }

            else -> {
                // 处理其他自定义方法
                KLogger.d { "JS method called: ${message.methodName} with params: ${message.params}" }
            }
        }
    }


    fun evaluateJavascript(script: String, callback: ((String) -> Unit)? = null) {
        KLogger.d { "Evaluating JavaScript: $script" }
        webView.evaluateJavascript(script) { result ->
            callback?.invoke(result)
        }
    }

    /**
     * 示例：调用HTML中的JS函数
     * 用法类似于桌面平台的: webView.engine.executeScript("renderLatex('$latex');")
     */
    fun callJSFunction(functionCall: String) {
        evaluateJavascript(functionCall)
    }

    /**
     * 调用JavaScript函数并传入参数
     * @param functionName 函数名
     * @param params 参数列表
     */
    fun callJSFunction(functionName: String, vararg params: Any?) {
        val paramStrings = params.mapNotNull { it.toString() }
        val functionCall = "$functionName(${paramStrings.joinToString(", ")})"
        evaluateJavascript(functionCall)
    }

    /**
     * 执行JavaScript代码并返回结果
     * @param script 要执行的JavaScript代码
     * @return 执行结果
     */
    suspend fun executeScript(script: String) {
        return evaluateJavascript(script) {
            // 返回结果
        }
    }

    /**
     * 注册一个JavaScript回调函数
     * @param callbackName 回调函数名
     * @param callback 回调函数
     */
    fun registerCallback(callbackName: String, callback: (String) -> Unit) {
        val callbackScript = """
            if (!window.$callbackName) {
                window.$callbackName = function(data) {
                    if (window.KotlinBridge) {
                        window.KotlinBridge.postMessage(JSON.stringify({
                            method: '$callbackName',
                            params: [data]
                        }));
                    }
                };
            }
        """.trimIndent()

        evaluateJavascript(callbackScript)
    }

    /**
     * 移除已注册的JavaScript回调函数
     * @param callbackName 回调函数名
     */
    fun removeCallback(callbackName: String) {
        val removeScript = """
            if (window.$callbackName) {
                window.$callbackName = null;
            }
        """.trimIndent()

        evaluateJavascript(removeScript)
    }
}
