package com.example.guilongyuangaoyue.myapplication.util

import android.util.Log
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class UnCatchExceptionHandler : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(p0: Thread, p1: Throwable) {
        Log.e("***********************************","thread name  "+p0.name)
        Log.e("***********************************","localizedMessage  "+p1.localizedMessage)
        Log.e("***********************************","message  "+p1.message)
        Log.e("***********************************","cause  "+p1.cause.toString())
    }

    fun postToServer(url : String, json : String) : String?{
        val JSON = "application/json; charset=utf-8".toMediaType()
        val body = json.toRequestBody(JSON);
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        var response = OkHttpClient().newCall(request).execute()
        return response.body?.string()
    }
}