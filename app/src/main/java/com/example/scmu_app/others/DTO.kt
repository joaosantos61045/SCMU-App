package com.example.scmu_app.others

import com.google.gson.annotations.Expose

data class User (
    val id: String,
    var boards: MutableList<UserBoard>
)

data class UserBoard(
    val board: String,
    val name: String,
    val notifications: Boolean
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
    @Expose(serialize = false,deserialize = false) var lastFetch: Long

    ) {
   fun isOnline():Boolean{return System.currentTimeMillis()/1000 - lastUpdate < 5  }
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
    val events: List<Event>
)