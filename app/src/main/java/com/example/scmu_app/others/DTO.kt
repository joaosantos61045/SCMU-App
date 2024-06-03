package com.example.scmu_app.others

import android.icu.util.TimeZone
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Calendar

data class User(
    val id: String,
    var boards: MutableList<UserBoard>
)

data class UserBoard(
    val board: String,
    var name: String,
    var notifications: Boolean
)

data class Board(
    var id: String,
    var active: Boolean,
    var duration: Int?,
    var hourToStart: Int?,
    var rotation: MutableList<Boolean>,
    var state: Int,
    var currentState: Int,
    var currentDate: Long,
    val lastUpdate: Long,
    var smart: Boolean,
    val currentTemp: Float,
    val currentHum: Float,
    private val timeZone: String?,
    var password : String,
    @Transient var lastFetch: Long

) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getLocalTimeZone(): ZoneId {
        if (timeZone == null)
            return ZoneOffset.UTC
        return ZoneId.of(timeZone)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isOnline(): Boolean {
        val time = ZonedDateTime.now(getLocalTimeZone())
        val tt = time.toEpochSecond()+time.offset.totalSeconds
        return tt - lastUpdate < 15
    }

    fun getStates(): String {
        val statusList = listOf("Running", "Paused", "Waiting")
        return statusList[currentState]
    }
}

data class Event(
    val start: Long,
    val end: Long,
    val executionTime: Int,
    val pausedTime: Int,
    val avgTemp: Double,
    val avgHum: Double,
    val timeLine: List<TimeLine>,
    val eventState: Int
) {
    fun getStates(): String {
        val statusList = listOf("User canceled", "Auto canceled", "Not Scheduled", "Scheduled")
        return statusList[eventState]
    }
}

data class TimeLine(
    val start: Long,
    val end: Long,
    val duration: Int,
    val state: Int
)

data class BoardInfo(
    val board: Board,
    val events: MutableList<Event>
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun eventsChanged(other: MutableList<Event>?): Boolean {
        if (other == null) return true
        if (events.size != other.size)
            return true

        for (i in 0..<events.size) {
            val thisTime = getDateTime(events[i].start)
            val otherTime = getDateTime(other[i].start)

            if (thisTime != otherTime)
                return true
        }

        return false
    }
}

data class WIFICred(
    val ssid: String,
    val pwd: String
)