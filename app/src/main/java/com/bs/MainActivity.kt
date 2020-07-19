package com.bs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.*
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bs.login.logInActivity
import com.bs.main.shopFragment
import com.bs.mine.mineFragment
import com.example.guilongyuangaoyue.myapplication.fouthFragment
import com.example.guilongyuangaoyue.myapplication.thirdFragment
import com.example.guilongyuangaoyue.myapplication.util.SaveView
import com.example.guilongyuangaoyue.myapplication.util.viewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.dialog.*
import kotlinx.android.synthetic.main.layout_main.*
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : FragmentActivity() {
    private lateinit var fragmentList : MutableList<Fragment>
    private val saveview =SaveView()
    private lateinit var bitmapModel : viewModel
    private var permission : Array<String> = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_main)
        //Thread.setDefaultUncaughtExceptionHandler(UnCatchExceptionHandler())

        this.requestPermissions(permission,100)
        bitmapModel= ViewModelProviders.of(this).get(viewModel::class.java)

        fragmentList = mutableListOf(shopFragment(),
            mineFragment(),thirdFragment(), fouthFragment())

        if(findViewById<View>(R.id.viewpager) != null){
            if (savedInstanceState != null) {
                return;
            }
        }


        //supportFragmentManager.beginTransaction().add(R.id.viewpager, fragmentList[0]).commit()
        //viewpager.isUserInputEnabled=false
        class viewPagerAdapter : FragmentStateAdapter(this){
            override fun getItemCount(): Int {
                return fragmentList.size
            }

            override fun createFragment(position: Int): Fragment {
                return fragmentList[position]
            }

        }
        viewpager.adapter=viewPagerAdapter()
        viewpager.currentItem = 0
        viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                //Log.e("**********************","state changed")
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                //Log.e("**********************","select")
                bnv.menu.getItem(position).setChecked(true)
            }
        })

        val myToast = Toast(this)
        val myToastView = LayoutInflater.from(this@MainActivity)
                .inflate(R.layout.toast,null) as RelativeLayout
        myToast.setGravity(Gravity.CENTER,0,0)
        myToast.view = myToastView
        val toastText = myToast.view.findViewById<TextView>(R.id.toastText)
        toastText.text ="error"
        toastText.textSize = 50f

        val dialog= Dialog(this)
        dialog.setContentView(R.layout.dialog)
        val handler=object:Handler(Looper.myLooper()){
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                val m= msg.data?.getString("tips")
                Log.e("mess  ",m)
                dialog.dialog_content.text=m
                dialog.show()
            }
        }
        fun run(url:String) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().get()
                        .url(url)
                        .build()

                val response = client.newCall(request)
                val call = client.newCall(request)
                var result=""
                //异步请求
                call.enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.e("UPDATE", "onFailure: $e")


                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        val s= response.body?.string()
                        Log.e("UPDATE", "OnResponse: " + s)

                        val message=Message()
                        val bundle=Bundle()
                        bundle.putString("tips",s)
                        message.data=bundle
                        handler.sendMessage(message)
                    }
                })
            }catch (e:Exception) {
                Log.e("UPDATE ERROR:", "", e)
            }
        }
        run("http://47.115.36.237:8000/annoce")

        fun saveBitmap(bitName: String, bitmap: Bitmap) {
            Thread(Runnable {
                val filePath = Environment.getExternalStorageDirectory().path
                val file = File("$filePath/DCIM/Camera/$bitName.png")
                try {
                    file.createNewFile()
                    var fOut: FileOutputStream? = null
                    fOut = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
                    //Toast.makeText(PayCodeActivity.this, "保存成功", Toast.LENGTH_LONG).show();
                    fOut.flush()
                    fOut.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }).start()
        }
        @SuppressLint("RestrictedApi")
        fun showPopupMenu(view: View) {
            val popupMenu = PopupMenu(this, view)
            var navigationItem = bnv.selectedItemId
            menuInflater.inflate(R.menu.popmenu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.vs -> {
                        Log.e("vs  ","menu")
                        startActivity(Intent(applicationContext,
                            logInActivity::class.java))
                        true
                    }
                    R.id.vm -> {
                        Log.e("vm  ","menu")
                        val bitmap=saveview.getBitmapFromShowedView(viewpager[viewpager.currentItem].rootView)
                        bitmapModel.setBitmap(bitmap)
                        Log.e("*******************************cut bitmap",bitmap.byteCount.toString())
                        saveBitmap("cut",bitmap)
                        true
                    }
                    else -> true
                }
            }

            val menuHelper = MenuPopupHelper(this, popupMenu.menu as MenuBuilder, view).also {
                it.setForceShowIcon(true)//设置显示图标
                it.show()
            }

        }
/*
        fun playObjectAnimation(obj: Object){
            val set = AnimatorSet()
            //控制透明度的，我用ofInt没好使，还是用float吧
            var animator1= ObjectAnimator.ofFloat(obj,"alpha",0f,1f)
            animator1?.duration=2000
            //延X轴移动
            var animator2=ObjectAnimator.ofFloat(obj,"translationX",0f,300f)
            animator2?.duration=2000
            animator2?.interpolator= DecelerateInterpolator()
            //延y轴移动,起始位置和结束位置
            var animator3=ObjectAnimator.ofFloat(obj,"translationY",0f,500f,200f)
            animator3?.duration=2000
            animator3?.interpolator=DecelerateInterpolator()
            //沿X轴旋转
            var animator4=ObjectAnimator.ofFloat(obj,"rotationX",0f,720f)
            animator4?.duration=2000
            //沿Y轴旋转
            var animator5=ObjectAnimator.ofFloat(obj,"rotationY",0f,720f)
            animator5?.duration=2000
            //缩放,可以根据X轴和Y轴缩放
            var animator6=ObjectAnimator.ofFloat(obj,"scaleX",0f,4f,2f)
            animator6?.duration=3000
            //改变颜色
            //var animator7=ObjectAnimator.ofInt(photo_id,"backgroundColor",Color.RED, Color.BLUE, Color.GRAY, Color.GREEN)
            //animator7?.duration=2000
            set.play(animator1).before(animator2) //先执行anim动画之后在执行anim2
            set.play(animator2).before(animator3)
            set.play(animator3).before(animator4)
            set.play(animator4).before(animator5)
            set.play(animator5).before(animator6)
            //set.play(animator6).before(animator7)
            set.start()
        }
*/
        fb.setOnClickListener(View.OnClickListener {
            Log.e("fb  ","click")
            showPopupMenu(fb)
        })

        val change = BottomNavigationView.OnNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.item1 -> {
                    bnv.menu.getItem(0).setChecked(true)
                    /*
                    supportFragmentManager.beginTransaction().apply{
                        replace(R.id.viewpager, fragmentList[0])
                        addToBackStack(null)
                    }.commit()
                     */
                    viewpager.currentItem = 0
                }
                R.id.item2 -> {
                    bnv.menu.getItem(1).setChecked(true)
                    /*
                    supportFragmentManager.beginTransaction().apply{
                        replace(R.id.viewpager, fragmentList[1])
                        addToBackStack(null)
                    }.commit()

                     */
                    viewpager.currentItem = 1
                }
                R.id.item3 -> {
                    bnv.menu.getItem(2).setChecked(true)
                    /*
                    supportFragmentManager.beginTransaction().apply{
                        replace(R.id.viewpager, fragmentList[2])
                        addToBackStack(null)
                    }.commit()

                     */
                    viewpager.currentItem = 2
                }
                R.id.item4 -> {
                    bnv.menu.getItem(3).setChecked(true)
                    /*
                    supportFragmentManager.beginTransaction().apply{
                        replace(R.id.viewpager, fragmentList[3])
                        addToBackStack(null)
                    }.commit()

                     */
                    viewpager.currentItem = 3
                    //myToast.show()
                }
                else -> {
                    myToast.show()
                }
            }
            false
        }
        //bnv.itemRippleColor = ColorStateList.valueOf(resources.getColor(R.color.colorAccent))//波纹颜色
        //bnv.itemTextColor = ColorStateList.valueOf(resources.getColor(R.color.colorAccent))
        //bnv.itemIconTintList = ColorStateList.valueOf(resources.getColor(R.color.colorAccent))
        bnv.setOnNavigationItemSelectedListener(change)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for(grant in grantResults){
            if(grant==PackageManager.PERMISSION_GRANTED){

            }
                //Log.e("****************************************** per",permissions.toString())
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this).setMessage("确定要离开咩~").setPositiveButton("离开") {
            p0, p1 -> android.os.Process.killProcess(android.os.Process.myPid()) }
                .setNegativeButton("不要",object : DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {

                    }
                }).show()

        //super.onBackPressed()
    }

}
