package com.example.scmu_app.others

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun getDateTime(timestampSeconds: Long): LocalDateTime {
    val instant = Instant.ofEpochSecond(timestampSeconds) // Convert seconds to Instant
    val dateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
    return dateTime
}
@RequiresApi(Build.VERSION_CODES.O)
fun dateToStandardFormat(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return dateTime.format(formatter)
}