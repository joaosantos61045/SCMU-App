package com.example.scmu_app.others

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.provider.Settings
import com.google.gson.Gson

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
    getRequest(
        "$URL/rest/boards/arduino01/info?days=7",
        onFailure = {
            onFailure()
        },
        onSuccess = {
            it.body?.string().let { content ->
                onSuccess(gson.fromJson(content, BoardInfo::class.java))
            }
        })

}

fun fetchFindBoard( arduino: String, pwd:String, onFailure: () -> Unit, onSuccess: (Board) -> Unit) {

    val gson = Gson()
    getRequest(
        "$URL/rest/boards/$arduino/pwd?pwd=$pwd",
        onFailure = {
            onFailure()
        },
        onSuccess = {
            it.body?.string().let { content ->
                onSuccess(gson.fromJson(content, Board::class.java))
            }
        })

}

fun cancelEvent( status: Int){

    val gson = Gson()
    putRequest("$URL/rest/boards/arduino01/request?request=$status", onFailure = { }, onSuccess = {}, requestBody =null)


}
