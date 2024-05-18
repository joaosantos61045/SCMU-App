package com.example.scmu_app.others

data class User(
    val id: String,
    val boards: MutableList<UserBoard>
)

data class UserBoard(
    val board: String,
    val name: String,
    val notifications: Boolean
)

data class Board(
    val id: String,
    val active: Boolean,
    val duration: Int,
    val hourToStart: Int,
    val rotation: List<Boolean>,
    var state: Int,
    val currentState: Int,
    val lastUpdate: Long,

    ) {
   fun isOnline():Boolean{return System.currentTimeMillis()/1000 - lastUpdate < 20  }
    fun getStates(): String {
        val statusList = listOf("Running", "Paused", "Waiting")
        return if(isOnline())
            statusList[currentState]
        else
            "Offline"
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

data class Request(
    val state: Int=2
)