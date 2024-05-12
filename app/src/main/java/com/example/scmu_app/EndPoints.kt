package com.example.scmu_app

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContextWrapper
import android.provider.Settings
import com.example.scmu_app.objects.User
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
