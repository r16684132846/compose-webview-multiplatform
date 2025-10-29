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
    return datetime.toString().replace("T", " ")
}

