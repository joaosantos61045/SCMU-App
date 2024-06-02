package com.example.scmu_app.others

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
fun getDateTime(timestampSeconds: Long): LocalDateTime {
    val instant = Instant.ofEpochSecond(timestampSeconds) // Convert seconds to Instant
    val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    return dateTime
}

@RequiresApi(Build.VERSION_CODES.O)
fun dateToStandardFormat(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return dateTime.format(formatter)
}

@RequiresApi(Build.VERSION_CODES.O)
fun calculateRemainingTime(currentDate: LocalDateTime, workDays: MutableList<Boolean>, startHour: Int): Long{
    val nextWorkingDay = getNextWorkingDay(currentDate, workDays)

    if(nextWorkingDay != null) {
        val nextEventDateTime =
            nextWorkingDay.withHour(startHour / 60).withMinute(startHour % 60).withSecond(0)
        val remainingTime= nextEventDateTime.atZone(ZoneId.systemDefault()).toEpochSecond() -
                currentDate.atZone(ZoneId.systemDefault()).toEpochSecond()
        return remainingTime
    }

    return 0L
}

@RequiresApi(Build.VERSION_CODES.O)
fun getNextWorkingDay(currentDate: LocalDateTime, workDays: MutableList<Boolean>): LocalDateTime? {
    var count = 0
    var nextDay = currentDate.plusDays(1)
    while (!workDays[nextDay.dayOfWeek.value - 1] && count < 7) {
        nextDay = nextDay.plusDays(1)
        count++
    }

    if(count >= 7)
        return null

    return nextDay
}

fun formatDuration(seconds: Long): String {
    val days = seconds / (24 * 3600)
    val hours = (seconds % (24 * 3600)) / 3600
    val minutes = (seconds % 3600) / 60

    val formattedString = StringBuilder()

    if (days > 0)
        formattedString.append("$days day ")
    if (hours > 0)
        formattedString.append("$hours hour ")
    if (minutes > 0)
        formattedString.append("$minutes mins")

    return formattedString.toString()
}
