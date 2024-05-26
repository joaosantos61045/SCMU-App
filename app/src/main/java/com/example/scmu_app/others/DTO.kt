package com.example.scmu_app.others

import com.google.gson.annotations.Expose
import android.os.Build
import androidx.annotation.RequiresApi

data class User (
    val id: String,
    var boards: MutableList<UserBoard>
)

data class UserBoard(
    val board: String,
    val name: String,
    var notifications: Boolean
)

data class Board(
    var id: String,
    var active: Boolean,
    var duration: Int,
    var hourToStart: Int,
    var rotation: MutableList<Boolean>,
    var state: Int,
    var currentState: Int,
    var currentDate: Long,
    var lastUpdate: Long,
    @Transient var lastFetch: Long

    ) {
   fun isOnline():Boolean{return System.currentTimeMillis()/1000 - lastUpdate < 10  }
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
    fun eventsChanged(other: MutableList<Event>): Boolean {
        if(events.size != other.size)
            return true

        for(i in 0..<events.size) {
            val thisTime = getDateTime(events[i].start)
            val otherTime = getDateTime(other[i].start)

            if(thisTime != otherTime)
                return true
        }

        return false
    }
}