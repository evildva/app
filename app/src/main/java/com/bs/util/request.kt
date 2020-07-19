package com.example.guilongyuangaoyue.myapplication.util

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException

class request(private val context: Context) {
    private lateinit var client : OkHttpClient
    private lateinit var request: Request

    fun request(activity : Activity,url : String,sync : Boolean){
        client=OkHttpClient()
        request=Request.Builder().url(url).build()
        if(sync){
            Thread(kotlinx.coroutines.Runnable {
                val response=client.newCall(request).execute()
                val gson= GsonBuilder().setLenient().enableComplexMapKeySerialization().serializeNulls().setPrettyPrinting().create()
                Log.e("*********************************************** json",gson.fromJson<urlList>(response.body?.string(),urlList::class.javaObjectType).toString())
            })
        }else
        client.newCall(request).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                activity.runOnUiThread(kotlinx.coroutines.Runnable {
                    AlertDialog.Builder(context)
                            .setTitle("net error")
                            .setMessage("request fail")
                            .setPositiveButton("retry", DialogInterface.OnClickListener {
                                dialogInterface, i ->  request(activity,url,sync)
                            })
                })

            }

            override fun onResponse(call: Call, response: Response) {
                val gson= GsonBuilder().setLenient().enableComplexMapKeySerialization().serializeNulls().setPrettyPrinting().create()
                Log.e("***********************************************",gson.fromJson<urlList>(response.body?.string(),urlList::class.javaObjectType).toString())
            }

        })
    }

    data class urlList(
            val a   : String,
            val urls: MutableList<String>,
            val nums: Int
    )

}