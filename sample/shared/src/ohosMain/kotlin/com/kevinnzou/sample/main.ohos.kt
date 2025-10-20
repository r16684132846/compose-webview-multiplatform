package com.kevinnzou.sample

import androidx.compose.runtime.Composable

actual fun getPlatformName(): String = "OHOS"

@Composable
fun MainWebView() = WebViewApp()
