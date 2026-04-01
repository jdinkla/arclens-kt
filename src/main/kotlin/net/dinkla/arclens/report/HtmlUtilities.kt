package net.dinkla.arclens.report

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Escapes special HTML characters to prevent XSS and rendering issues.
 */
internal fun escapeHtml(text: String): String =
    text
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;")

/**
 * Formats a Unix timestamp (milliseconds) into a human-readable date/time string.
 */
internal fun formatTimestamp(timestamp: Long): String {
    if (timestamp == 0L) return "N/A"
    val instant = Instant.ofEpochMilli(timestamp)
    val formatter =
        DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault())
    return formatter.format(instant)
}

/**
 * Formats a number with thousands separators.
 */
internal fun formatNumber(number: Int): String = "%,d".format(number)

/**
 * Formats a double value to a specified number of decimal places.
 */
internal fun formatDouble(
    value: Double,
    decimals: Int = 2,
): String = "%.${decimals}f".format(value)

/**
 * Creates a safe HTML ID from a string (e.g., package name).
 */
internal fun toHtmlId(text: String): String =
    text
        .replace(".", "-")
        .replace(" ", "-")
        .lowercase()
