package com.example.guilongyuangaoyue.myapplication.login

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bs.MainActivity
import com.bs.R
import com.example.guilongyuangaoyue.myapplication.thirdFragment
import com.example.guilongyuangaoyue.myapplication.util.sharedPreferences
import kotlinx.android.synthetic.main.layout_login_login.*
import okhttp3.*
import java.io.IOException

class login : Fragment()  {

    val handler = Handler(){
        when(it.what){
            1->{
                lleditname.setTextColor(Color.BLACK)
            }
            2->{
                lleditname.setTextColor(Color.RED)
            }
            3->{
                Toast.makeText(context,"登陆成功",Toast.LENGTH_SHORT).show()
            }
        }
        false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_login_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lleditname.setOnFocusChangeListener { view, b ->  if(b) lleditname.hint="" else checkName()}
        lleditpass.setOnFocusChangeListener { view, b ->  if(b) lleditpass.hint="" else checkNull()}
        login_but.setOnClickListener { signin() }
        login_check.setOnCheckedChangeListener { compoundButton, b ->
            if(b) {
                activity?.applicationContext?.let { sharedPreferences(it).saveValue("keep","yes")
                }
            }
            else
                {activity?.applicationContext?.let { sharedPreferences(it).saveValue("keep","no")}}}
        lltv.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.login_vp,thirdFragment())?.commit()
        }

        super.onViewCreated(view, savedInstanceState)
    }

    fun checkName(){
        val client = OkHttpClient()
        val requestBody = FormBody.Builder().add("name",lleditname.text.toString()).build()
        val request = Request.Builder().url("http://47.115.36.237:8000/login/check").post(requestBody).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                lleditname.post { AlertDialog.Builder(context).setMessage("网络出错啦~").show() }
            }

            override fun onResponse(call: Call, response: Response) {
                val str=response.body?.string()
                Log.e("----------------------------", str)
                val msg=handler.obtainMessage()
                if(str=="ok") {
                    msg.what = 1
                    handler.sendMessage(msg)
                }
                if(str=="no"){
                    msg.what=2
                    handler.sendMessage(msg)
                }
            }
        })
    }

    fun checkNull(){

    }

    fun signin(){
        val client = OkHttpClient()
        val requestBody = FormBody.Builder().add("name",lleditname.text.toString()).add("pass",lleditpass.text.toString()).build()
        val request = Request.Builder().url("http://47.115.36.237:8000/login/sigin").post(requestBody).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                lleditname.post { AlertDialog.Builder(context).setMessage("登陆出错啦~").show() }
            }

            override fun onResponse(call: Call, response: Response) {
/*
                AlertDialog.Builder(context).setMessage("那个~ 你已经登陆了哟~").setPositiveButton("进商店看看吧") {
                        p0, p1 ->  }
                    .setNegativeButton("我手抖了，点错了",object : DialogInterface.OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {

                        }
                    }).show()

 */
                val msg=handler.obtainMessage()
                msg.what=3
                handler.sendMessage(msg)
                Log.e("-----------------------------"," login "+ response.body!!.string())
            }

        })
        val code="username&"+lleditname.text.toString()+"#pass&"+lleditpass.text.toString()+"#acess&"
        activity?.applicationContext?.let { sharedPreferences(it).saveValue("user",code) }
    }
}//  Set-Cookie: mycookiesessionnameid=48de53a9-6bae-4685-9bcb-421c5f326ee6;