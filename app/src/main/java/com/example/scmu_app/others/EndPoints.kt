package com.example.scmu_app.others

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.provider.Settings
import android.util.Log
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

fun fetchBoardInfo(sysId:String, onFailure: () -> Unit, onSuccess: (BoardInfo) -> Unit) {

    val gson = Gson()
    getRequest(
        "$URL/rest/boards/"+sysId+"/info?days=7",
        onFailure = {
            onFailure()
        },
        onSuccess = {
            it.body?.string().let { content ->
                onSuccess(gson.fromJson(content, BoardInfo::class.java))
            }
        })

}

fun fetchFindBoard( arduino: String,  onFailure: () -> Unit, onSuccess: (Board) -> Unit) {

    val gson = Gson()
    getRequest(
        "$URL/rest/boards/$arduino/",
        onFailure = {
            onFailure()
        },
        onSuccess = {
            it.body?.string().let { content ->
                onSuccess(gson.fromJson(content, Board::class.java))
            }
        })

}

fun cancelEvent(status: Int, id: String){

    val gson = Gson()
    putRequest("$URL/rest/boards/"+id+"/request?request=$status", onFailure = { }, onSuccess = {}, requestBody =null)


}
fun updateBoard( board: Board ,onFailure: () -> Unit, onSuccess: (Board) -> Unit){

    val gson = Gson()



    putRequest("$URL/rest/boards/"+board.id+"/user",
        onFailure = {onFailure() },
        onSuccess = { it.body?.string().let { content ->
            onSuccess(gson.fromJson(content, Board::class.java)) } },
        requestBody = gson.toJson(board))
}
fun updateUser(context: ContentResolver, onFailure: () -> Unit, onSuccess: (User) -> Unit,user:User) {

    val androidId = Settings.Secure.getString(context, Settings.Secure.ANDROID_ID)
    val gson = Gson()
    putRequest(
        "$URL/rest/users/$androidId",
        requestBody = gson.toJson(user),
        onFailure = {
            onFailure()
        },
        onSuccess = {
            it.body?.string().let { content ->
                onSuccess(gson.fromJson(content, User::class.java))
            }
        })

}