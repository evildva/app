package com.example.guilongyuangaoyue.myapplication.util

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SaveView {
    fun getBitmapFromShowedView(view:  View) : Bitmap{
        val width=view.width
        val height=view.height

        val bitmap=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
        val canvas=Canvas(bitmap)
        view.layout(0,0,width,height)
        view.draw(canvas)

        return bitmap
    }

    fun getBitmapFromPrepareView(activity: Activity, layoutid : Int) : Bitmap {
        val metrics= DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        val width=metrics.widthPixels
        val height=metrics.heightPixels
        val view=LayoutInflater.from(activity.applicationContext).inflate(layoutid,null,false)
        view.layout(0,0,width,height)
        view.measure(View.MeasureSpec.makeMeasureSpec(width,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(height,View.MeasureSpec.AT_MOST))
        view.layout(0,0,view.measuredWidth,view.measuredHeight)

        return getBitmapFromShowedView(view)
    }

    private fun hasSDcard() : Boolean{
        return android.os.Environment.getExternalStorageState() == android.os.Environment.MEDIA_MOUNTED
    }
}

class viewModel : ViewModel(){
    private var bitmap = MutableLiveData<Bitmap>()

    fun setBitmap(bit: Bitmap){
        Log.e("*******************************set bitmap",bit.byteCount.toString())
        bitmap.value=bit
    }

    fun getBitmap() : MutableLiveData<Bitmap>{
        Log.e("*******************************get bitmap",bitmap.value.toString())
        return bitmap
    }
}