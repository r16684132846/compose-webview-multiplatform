package com.multiplatform.webview.cookie

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Get cookie expiration date.
 * @param expiresDate The cookie expiration date in milliseconds.
 * @return The cookie expiration date in [String] format.
 * */
actual fun getCookieExpirationDate(expiresDate: Long): String {
    val instant = Instant.fromEpochMilliseconds(expiresDate)
    val datetime = instant.toLocalDateTime(TimeZone.UTC)
    // 格式化为符合Cookie标准的日期格式：EEE, dd MMM yyyy HH:mm:ss z
    return datetime.toString().replace("T", " ")
}

