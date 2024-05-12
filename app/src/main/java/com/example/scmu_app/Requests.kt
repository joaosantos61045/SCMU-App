package com.example.scmu_app

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

fun postRequest(
    endpoint: String,
    requestBody: String,
    onFailure: (exception: IOException) -> Unit,
    onSuccess: (response: Response) -> Unit
) {

    val client = OkHttpClient()

    val mediaType = "application/json; charset=utf-8".toMediaType()
    val requestBody = requestBody.toRequestBody(mediaType)

    val requestBuilder = Request.Builder()
        .url(endpoint)
        .post(requestBody)

    val request = requestBuilder.build()
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            onFailure(e)
        }

        override fun onResponse(call: Call, response: Response) {
            onSuccess(response)
        }
    })


}