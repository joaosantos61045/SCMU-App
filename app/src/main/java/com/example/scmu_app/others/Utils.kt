package com.example.scmu_app.others

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
fun getDateTime(timestampSeconds: Long): LocalDateTime {
    return LocalDateTime.ofEpochSecond(timestampSeconds,0,  ZoneOffset.ofTotalSeconds(0))
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

fun formatDuration(seconds: Long, showSeconds : Boolean = false): String {
    val days = seconds / (24 * 3600)
    val hours = (seconds % (24 * 3600)) / 3600
    val minutes = (seconds % 3600) / 60
    val sec = seconds % 60

    val formattedString = StringBuilder()

    if (days > 0)
        formattedString.append("$days day ")
    if (hours > 0)
        formattedString.append("$hours hour ")
    if (minutes > 0)
        formattedString.append("$minutes mins ")
    if (sec >= 0 && showSeconds)
        formattedString.append("$sec secs")

    return formattedString.toString()
}


@RequiresApi(Build.VERSION_CODES.O)
fun convertTo(minutesSinceMidnight: Int, sourceZone: ZoneId, targetZone:ZoneId): Int {
    val localDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(minutesSinceMidnight / 60, minutesSinceMidnight % 60))

    val sourceZonedDateTime = localDateTime.atZone(sourceZone)
    val targetZonedDateTime = sourceZonedDateTime.withZoneSameInstant(targetZone)

    val utcHours = targetZonedDateTime.hour
    val utcMinutes = targetZonedDateTime.minute

    return utcHours * 60 + utcMinutes
}
