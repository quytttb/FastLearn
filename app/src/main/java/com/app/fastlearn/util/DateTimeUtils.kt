package com.app.fastlearn.util

import android.util.Log
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.FormatStyle
import java.util.Locale

object DateTimeUtils {
    private const val TAG = "DateTimeUtils"

    /**
     * Định dạng ngày tháng bằng cách chuyển đổi từ định dạng ISO-8601 sang định dạng đọc hiểu được
     *
     * @param dateString Chuỗi ngày tháng nhập vào (Định dạng: "YYYY-MM-DDTHH: MM: SS")
     * @return Chuỗi ngày tháng được định dạng (Định dạng: "DD-MM-YYYY")
     */
fun formatDate(dateString: String): String {
    return try {
        val isoDate = dateString.substringBefore("T")
        val date = LocalDate.parse(isoDate)
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
            .withLocale(Locale.getDefault())
        date.format(formatter)
    } catch (e: DateTimeParseException) {
        Log.e(TAG, "DateTimeParseException in formatDate: $dateString", e)
        dateString
    } catch (e: Exception) {
        Log.e(TAG, "Exception in formatDate: $dateString", e)
        dateString
    }
}
}