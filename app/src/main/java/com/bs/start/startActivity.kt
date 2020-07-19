package com.example.guilongyuangaoyue.myapplication.start

import android.content.Intent
import android.os.Bundle
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.bs.MainActivity
import com.bs.R
import kotlinx.android.synthetic.main.layout_login_thirdparty.*
import kotlinx.android.synthetic.main.layout_start.*

import java.util.*

class startActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_start)

        val setting=start_web.settings
        setting.javaScriptEnabled=true
        setting.javaScriptCanOpenWindowsAutomatically=true
        start_web.webViewClient=object : WebViewClient(){

        }
        webview.loadUrl("http://47.115.36.237:8800/start")
    }

    override fun onStart() {
        super.onStart()

        Timer().schedule(object  : TimerTask(){
            override fun run() {
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }
        },3000)
    }
}