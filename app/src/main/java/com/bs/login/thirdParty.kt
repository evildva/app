package com.example.guilongyuangaoyue.myapplication.login

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bs.R
import com.example.guilongyuangaoyue.myapplication.util.viewModel
import kotlinx.android.synthetic.main.layout_login_thirdparty.*
import okhttp3.*
import java.io.IOException
import java.net.URL
import java.util.*

class thirdParty : Fragment()  {
    private lateinit var bitmapModel : viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bitmapModel= ViewModelProviders.of(this).get(viewModel::class.java)
        return inflater.inflate(R.layout.layout_login_thirdparty, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lateinit var data:String
        //activity?.applicationContext?.let { data=sharedPreferences(it).getString("abc")}
        //third_tv.text=data

        val client_id = "f4172c8f1924dd09c3a7"
        val clien_secret = "1a5b18302ac405216091034beb7ef72b1e5ec7ec"
        val redirect_url = "http://47.115.36.237:8800/"
        var accessToken=""
        val state = UUID.randomUUID().toString()
        val url="https://github.com/login/oauth/authorize?"+"client_id=${client_id}&"+"redirect_uri=${redirect_url}&"+"state=${state}"
        //https://github.com/login/oauth/authorize?client_id=f4172c8f1924dd09c3a7&redirect_uri=http://47.115.36.237:8800/&state=

        val setting=webview.settings
        setting.javaScriptEnabled=true
        setting.javaScriptCanOpenWindowsAutomatically=true
        webview.canGoBack()
        webview.webViewClient=object : WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                //view?.evaluateJavascript("var div=document.getElementsByTagName('div'); for(var i=0;i<div.length;i++){div[i].style=''}; document.getElementsByTagName(\"body\")[0].addEventListener(\"click\",function(event){var e = event || window.event;var x=e.clientX;var y=e.clientY;document.elementFromPoint(x,y).style='display:none;';})"
                //) { p0 -> Log.e("*********************************", p0) }
                super.onPageFinished(view, url)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return super.shouldOverrideUrlLoading(view, url)
            }

            override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
                Log.e("----------------------------should ",url)
                var state=""
                var code=""
                if (url != null) {
                    if(url.contains("47.115.36.237")){
                        for(str in url.removePrefix("http://47.115.36.237:8800/?").split("&")){
                            var m=str.split("=")
                            when(m[0]){
                                "state"->{
                                    state=m[1]
                                }
                                "code"->{
                                    code=m[1]
                                }
                                "access_token" ->{
                                    accessToken=m[1]
                                }
                                else ->{
                                    Log.e("-------------------------- second",url)
                                }
                            }
                        }

                        //val url="https://github.com/login/oauth/access_token?"+"client_id=${client_id}&"+"client_secret=${clien_secret}&"+"code=${code}"
                        //https://github.com/login/oauth/access_token?client_id=f4172c8f1924dd09c3a7&client_secret=1a5b18302ac405216091034beb7ef72b1e5ec7ec&code=b5f7b1279e93f51f6ea0
                        val okHttpClient = OkHttpClient()
                        val body = FormBody.Builder()
                        body.add("client_id", client_id)
                        body.add("client_secret", clien_secret)
                        body.add("code", code)
                        val request = Request.Builder().url("https://github.com/login/oauth/access_token").post(body.build()).build()
                        okHttpClient.newCall(request).enqueue(object : Callback{
                            override fun onFailure(call: Call, e: IOException) {
                                Log.e("--------------------------","oauth error")
                            }

                            override fun onResponse(call: Call, response: Response) {
                                val request= Request.Builder().url("https://api.github.com/users/evildva").addHeader("Authorization","${accessToken} OAUTH-TOKEN").get().build()
                                okHttpClient.newCall(request).enqueue(object : Callback{
                                    override fun onFailure(call: Call, e: IOException) {
                                        Log.e("--------------------------","info error")
                                    }

                                    override fun onResponse(call: Call, response: Response) {
                                        Log.e("-------------------------- first", response.body?.string())
                                    }
                                })
                            }
                        })
                    }
                }

                if (url != null) {
                    if(url.contains(" https://github.githubassets.com")){

                    }
                }
                return super.shouldInterceptRequest(view, url)
            }
        }
        webview.webChromeClient=object : WebChromeClient(){

            override fun onJsAlert(
                    view: WebView?,
                    url: String?,
                    message: String?,
                    result: JsResult?
            ): Boolean {
                return super.onJsAlert(view, url, message, result)
            }


        }
        webview.setOnLongClickListener(object : View.OnLongClickListener{
            override fun onLongClick(p0: View?): Boolean {
                val result: WebView.HitTestResult = (p0 as WebView).hitTestResult ?: return false
                when (result.type) {
                    WebView.HitTestResult.EDIT_TEXT_TYPE -> {
                        Log.e("*********************************","EDIT_TEXT_TYPE")
                    }
                    WebView.HitTestResult.PHONE_TYPE -> {
                        Log.e("*********************************","PHONE_TYPE")
                    }
                    WebView.HitTestResult.EMAIL_TYPE -> {
                        Log.e("*********************************","EMAIL_TYPE")
                    }
                    WebView.HitTestResult.GEO_TYPE -> {
                        Log.e("*********************************","GEO_TYPE")
                    }
                    WebView.HitTestResult.SRC_ANCHOR_TYPE -> {
                        Log.e("*********************************","SRC_ANCHOR_TYPE")
                    }
                    WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE, WebView.HitTestResult.IMAGE_TYPE -> {
                        Log.e("*********************************","SRC_IMAGE_ANCHORIMAGE_TYPE")
                    }
                    WebView.HitTestResult.UNKNOWN_TYPE -> {
                        Log.e("*********************************","UNKNOWN_TYPE")
                    }
                }
                return false
            }

        })

        but.setOnClickListener{
            webview.loadUrl(url)
        }

        super.onViewCreated(view, savedInstanceState)
    }
}