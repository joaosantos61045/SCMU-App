package com.example.scmu_app

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContextWrapper
import android.provider.Settings
import com.example.scmu_app.objects.User
import com.example.scmu_app.ui.theme.BoardInfo
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

val URL = "https://scmu.azurewebsites.net"
@SuppressLint("HardwareIds")
fun fetchUser(context: ContentResolver, onFailure: () -> Unit, onSuccess: (User) -> Unit) {
    val androidId = Settings.Secure.getString(context, Settings.Secure.ANDROID_ID)

    val gson = Gson()
    postRequest(
        "$URL/rest/users",
        requestBody = gson.toJson(User(androidId, mutableListOf())),
        onFailure = {
            onFailure()
        },
        onSuccess = {
            it.body?.string().let { content ->
                onSuccess(gson.fromJson(content, User::class.java))
            }
        })

}

fun fetchBoardInfo( onFailure: () -> Unit, onSuccess: (BoardInfo) -> Unit) {

    val gson = Gson()
    val json="""{
    "board": {
        "id": "arduino01",
        "active": true,
        "duration": 30,
        "hourToStart": 660,
        "rotation": [
            true,
            false,
            false,
            true,
            true,
            false,
            true
        ],
        "state": -1,
        "currentState": 2,
        "lastUpdate": 1715880081,
        "data": [],
        "status": []
    },
    "events": [
        {
            "start": 0,
            "end": 0,
            "executionTime": 0,
            "pausedTime": 0,
            "avgTemp": 25.600002,
            "avgHum": 57.0,
            "timeLine": [],
            "asEvent": false
        },
        {
            "start": 1716030000,
            "end": 1716032104,
            "executionTime": 1804,
            "pausedTime": 300,
            "avgTemp": 25.6,
            "avgHum": 57.0,
            "timeLine": [
                {
                    "start": 1716030000,
                    "end": 1716030604,
                    "duration": 604,
                    "state": 0
                },
                {
                    "start": 1716030604,
                    "end": 1716030904,
                    "duration": 300,
                    "state": 1
                },
                {
                    "start": 1716030904,
                    "end": 1716032104,
                    "duration": 1200,
                    "state": 0
                }
            ],
            "asEvent": true
        },
        {
            "start": 1716116400,
            "end": 1716118504,
            "executionTime": 1804,
            "pausedTime": 300,
            "avgTemp": 25.6,
            "avgHum": 57.0,
            "timeLine": [
                {
                    "start": 1716116400,
                    "end": 1716116557,
                    "duration": 157,
                    "state": 0
                },
                {
                    "start": 1716116557,
                    "end": 1716116857,
                    "duration": 300,
                    "state": 1
                },
                {
                    "start": 1716116857,
                    "end": 1716118504,
                    "duration": 1647,
                    "state": 0
                }
            ],
            "asEvent": true
        },
        {
            "start": 0,
            "end": 0,
            "executionTime": 0,
            "pausedTime": 0,
            "avgTemp": 25.66,
            "avgHum": 57.0,
            "timeLine": [],
            "asEvent": false
        },
        {
            "start": 0,
            "end": 0,
            "executionTime": 0,
            "pausedTime": 0,
            "avgTemp": 25.7,
            "avgHum": 57.0,
            "timeLine": [],
            "asEvent": false
        },
        {
            "start": 1716375600,
            "end": 1716378304,
            "executionTime": 1804,
            "pausedTime": 900,
            "avgTemp": 25.63571,
            "avgHum": 57.0,
            "timeLine": [
                {
                    "start": 1716375600,
                    "end": 1716376540,
                    "duration": 940,
                    "state": 0
                },
                {
                    "start": 1716376540,
                    "end": 1716376840,
                    "duration": 300,
                    "state": 1
                },
                {
                    "start": 1716376840,
                    "end": 1716376948,
                    "duration": 108,
                    "state": 0
                },
                {
                    "start": 1716376948,
                    "end": 1716377248,
                    "duration": 300,
                    "state": 1
                },
                {
                    "start": 1716377248,
                    "end": 1716377647,
                    "duration": 399,
                    "state": 0
                },
                {
                    "start": 1716377647,
                    "end": 1716377947,
                    "duration": 300,
                    "state": 1
                },
                {
                    "start": 1716377947,
                    "end": 1716378304,
                    "duration": 357,
                    "state": 0
                }
            ],
            "asEvent": true
        },
        {
            "start": 1716462000,
            "end": 1716463804,
            "executionTime": 1804,
            "pausedTime": 0,
            "avgTemp": 25.600002,
            "avgHum": 57.0,
            "timeLine": [
                {
                    "start": 1716462000,
                    "end": 1716463804,
                    "duration": 1804,
                    "state": 0
                }
            ],
            "asEvent": true
        },
        {
            "start": 0,
            "end": 0,
            "executionTime": 0,
            "pausedTime": 0,
            "avgTemp": 25.66,
            "avgHum": 57.0,
            "timeLine": [],
            "asEvent": false
        },
        {
            "start": 1716634800,
            "end": 1716636604,
            "executionTime": 1804,
            "pausedTime": 0,
            "avgTemp": 25.700006,
            "avgHum": 57.0,
            "timeLine": [
                {
                    "start": 1716634800,
                    "end": 1716636604,
                    "duration": 1804,
                    "state": 0
                }
            ],
            "asEvent": true
        }
    ]
}"""
    onSuccess(gson.fromJson(json, BoardInfo::class.java))//TODO replace endpoint
}
