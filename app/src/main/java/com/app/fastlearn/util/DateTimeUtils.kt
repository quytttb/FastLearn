package com.app.fastlearn.util

object DateTimeUtils {
    /**
     * Formats a date string by extracting the part before "T"
     * Used for converting ISO-8601 formatted dates to a simpler display format
     *
     * @param dateString The input date string (expected format: "yyyy-MM-ddTHH:mm:ss")
     * @return The formatted date string (format: "yyyy-MM-dd")
     */
    fun formatDate(dateString: String): String {
        return try {
            dateString.substringBefore("T")
        } catch (e: Exception) {
            dateString
        }
    }
}