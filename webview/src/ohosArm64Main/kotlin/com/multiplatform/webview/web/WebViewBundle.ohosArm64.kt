package com.multiplatform.webview.web

/**
 * Created By Kevin Zou On 2023/12/9
 */
actual class WebViewBundle actual constructor() {
    // 存储WebView的状态信息，如滚动位置等
    var scrollX: Int = 0
    var scrollY: Int = 0

    // 可以添加更多状态信息
    var userAgent: String = ""

    constructor(scrollX: Int, scrollY: Int, userAgent: String) : this() {
        this.scrollX = scrollX
        this.scrollY = scrollY
        this.userAgent = userAgent
    }

    // 添加复制构造函数
    constructor(bundle: WebViewBundle) : this() {
        this.scrollX = bundle.scrollX
        this.scrollY = bundle.scrollY
        this.userAgent = bundle.userAgent
    }
}
