package com.bs.main

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bs.R
import com.bumptech.glide.Glide
import com.example.guilongyuangaoyue.myapplication.util.request
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.layout_shop.*
import okhttp3.*
import java.io.IOException
import java.util.*

class shopFragment : Fragment() {

    val fragment : Fragment= this
    private var url= mutableListOf<String>()
    lateinit var timer : Timer
    lateinit var timerTask : TimerTask
    val handler= Handler(){
        when(it.what){
            1 -> {
                url= (it.obj as request.urlList).urls

                //shop_viewpager.adapter= context?.let { it1 -> viewPagerAdapter(url, it1) }
                timer.schedule(timerTask,2000,3000)
                //Log.e("************************ url list  ", url.toString())
            }
            2 -> {
                Log.e("************************ Connection refused  ", it.obj as String)
            }
            else -> {

            }
        }
        false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val client : OkHttpClient = OkHttpClient()
        val request: Request = Request.Builder().url("http://47.115.36.237:8000/shop/show").build()

        val response = client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                val message=handler.obtainMessage()
                message.what=2
                message.obj="Connection refused"
                handler.sendMessage(message)
            }

            override fun onResponse(call: Call, response: Response) {
                val gson = GsonBuilder().setLenient().enableComplexMapKeySerialization().serializeNulls().setPrettyPrinting().create()
                val urls=gson.fromJson<request.urlList>(response.body?.string(), request.urlList::class.javaObjectType)
                val message=handler.obtainMessage()
                message.what=1
                message.obj=urls
                handler.sendMessage(message)
            }
        })

        return inflater.inflate(R.layout.layout_shop,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val viewModel = ViewModelProviders.of(this).get(pageViewModel::class.java)
        val adapter = context?.let { pageListAdapter(it) }
        shop_recyclerview.layoutManager= LinearLayoutManager(context)
        shop_recyclerview.adapter=adapter
        viewModel.pageList.observe(viewLifecycleOwner, Observer { t ->
            adapter?.submitList(t)
        })

        timer = Timer()
        var times=0
        val handler = Handler(){

            when(it.what){
                3 ->{
                    context?.let {
                        Glide.with(it)
                                .asBitmap().skipMemoryCache(false)
                                .load(url[times%3]).into(shop_image)
                    }
                    times++
                }
                else ->{

                }
            }
            false
        }
        timerTask = object :TimerTask(){

            override fun run() {
                val message = handler.obtainMessage()
                message.what=3
                handler.sendMessage(message)
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    class viewPagerAdapter(/*urls: MutableList<String>,context: Context*/) : RecyclerView.Adapter<viewPagerAdapter.ViewHolder>(){
        //private var list = urls
        //private var context = context

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val imageView=itemView.findViewById<ImageView>(R.id.shop_item_imageview)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context) .inflate(R.layout.layout_shop_viewpager_item, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return 3
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.imageView.setImageResource(R.mipmap.lock)
            /*
            Glide.with(context)
                    .asBitmap().skipMemoryCache(true)
                    .load(list[position]).into(holder.imageView)

            Log.e("--------------------------------",list[position])
             */
        }
    }

    data class mData(val id : Int, val data : String)

    data class shop(val shopId : String, val introImage : String, val more : String, val shopName : String,val username : String)
    //data class shoplist(val shopList: MutableList<shop>)

    class pageDataSource: PageKeyedDataSource<Int, shop>(){

        fun getShopList(json : String) : MutableList<shop>{
            val shoplist= mutableListOf<shop>()
            if(json.isEmpty())
                return shoplist
            try {
                val gson =
                    GsonBuilder().setLenient().enableComplexMapKeySerialization().serializeNulls()
                        .setPrettyPrinting().create()
                val jsonArray = JsonParser.parseString(json).asJsonArray
                for (str in jsonArray) {
                    val ss = mutableListOf<String>()
                    for (item in str.toString().replace("{", "").replace("}", "").split(",")) {
                        ss.add(item.split(":")[1])
                    }
                    shoplist.add(shop(ss[0], ss[1], ss[2], ss[3], ss[4]))
                }
            }catch (e : Exception){
                println(e.localizedMessage)
            }

            return shoplist
        }

        override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, shop>) {
            val client = OkHttpClient()
            val request = Request.Builder().url("http://47.115.36.237:8000/shop/shoplist/"+0).build()
            client.newCall(request).enqueue(object : Callback{
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("--------------------------------","net error")
                }

                override fun onResponse(call: Call, response: Response) {
                    val result = response.body?.string()
                    /*
                    val gson = GsonBuilder().setLenient().enableComplexMapKeySerialization().serializeNulls().setPrettyPrinting().create()
                    //Log.e("-----------------------------","init result "+result.toString())
                    val jsonArray = JsonParser.parseString(result).asJsonArray
                    var shopList= mutableListOf<shop>()/*gson.fromJson<shoplist>(response.body?.string(), shop::class.javaObjectType)TypeToken.getParameterized(MutableList::class.java,shop::class.java).type)shoplist::class.javaObjectType)*/

                    for(s in jsonArray){
                        Log.e("-----------------------------",gson.fromJson(s,shop::class.javaObjectType).toString())
                        shopList.add(gson.fromJson<shop>(s,shop::class.java))
                    }
                    */
                    //Log.e("--------------------------------","init "+result)
                    val shopList= getShopList(result!!)
                    callback.onResult(shopList,null,1)
                }
            })
        }

        override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, shop>) {

            val client = OkHttpClient()
            val request = Request.Builder().url("http://47.115.36.237:8000/shop/shoplist/"+params.key).build()
            client.newCall(request).enqueue(object : Callback{
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("--------------------------------","net error")
                }

                override fun onResponse(call: Call, response: Response) {
                    val result= response.body?.string()
                    /*
                    val gson = GsonBuilder().setLenient().enableComplexMapKeySerialization().serializeNulls().setPrettyPrinting().create()
                    Log.e("--------------------------------","after result  "+result)
                    val jsonArray = JsonParser.parseString(result).asJsonArray
                    var shopList= mutableListOf<shop>()/*gson.fromJson<shoplist>(response.body?.string(), shop::class.javaObjectType)TypeToken.getParameterized(MutableList::class.java,shop::class.java).type)shoplist::class.javaObjectType)*/

                    for(s in jsonArray){
                        shopList.add(gson.fromJson<shop>(s,shop::class.javaObjectType))
                    }
                    */
                    val shopList=getShopList(result!!)
                    //Log.e("-------------------------------- "+params.key,"  after  "+shopList.toString())
                    callback.onResult(shopList,params.key+1)
                }
            })

        }

        override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, shop>) {
            //callback.onResult(getShopList(params.key),params.key-1)
            Log.e("--------------------------------","before")
        }

    }

    class pageDataFactory : androidx.paging.DataSource.Factory<Int, shop>(){
        override fun create(): androidx.paging.DataSource<Int, shop> {
            return pageDataSource()
        }

    }

    class pageViewModel : ViewModel(){
        val pageList= LivePagedListBuilder(pageDataFactory(), PagedList.Config.Builder().setEnablePlaceholders(true).setPageSize(3).build()).build()
    }

    class pageListAdapter(val context: Context) : PagedListAdapter<shop, pageListAdapter.viewHolder>(
        diffCallback
    ){
        companion object{
            val diffCallback=object :DiffUtil.ItemCallback<shop>(){
                override fun areItemsTheSame(oldItem: shop, newItem: shop): Boolean {
                    Log.e("--------------------------------","id   "+(oldItem.shopId==newItem.shopId))
                    return oldItem.shopId==newItem.shopId
                }

                override fun areContentsTheSame(oldItem: shop, newItem: shop): Boolean {
                    Log.e("--------------------------------","name   "+(oldItem.shopName==newItem.shopName))
                    return oldItem.shopName==newItem.shopName
                }

            }
        }

        class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val imageView=itemView.findViewById<ImageView>(R.id.shop_recycler_item_image)
            val textView=itemView.findViewById<TextView>(R.id.shop_recycler_item_tv)

            fun bind(m : shop, context: Context){
                var mhandler: Handler? = Handler(object : Handler.Callback {
                    override fun handleMessage(message: Message): Boolean {
                        if (message.what == 12) {
                            val picture = message.obj as ByteArray
                            val bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.size)
                            imageView.setImageBitmap(bitmap) //主线程修改UI
                        }
                        return true
                    }
                })

                try {
                    val okHttpClient = OkHttpClient()
                    //Log.e("-----------------------------url ", "http://47.115.36.237:8000/shopimage"+m.introImage.replace("\"",""))
                    val request = Request.Builder().url("http://47.115.36.237:8000/shopimage"+m.introImage.replace("\"","")).get().build() //默认为get请求
                    val call = request.let { okHttpClient.newCall(it) }
                    call.enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            Log.e("-----------------------------", "onFailure = $e")
                        }

                        @Throws(IOException::class)
                        override fun onResponse(call: Call, response: Response) {
                            val picture = response.body!!.bytes()
                            //Log.e("-----------------------------", "response = $picture")
                            val message = Message.obtain()
                            message.what = 12
                            message.obj = picture
                            mhandler?.sendMessage(message)
                        }
                    })
                }
                catch (e : Exception){
                    Log.e("-----------------------------",e.toString())
                }
/*
                Glide.with(context)
                        .asBitmap().skipMemoryCache(false)
                        .load(m!!.introImage.replace('*',':')).into(imageView)
*/
                textView.text=m.shopName.replace("\"","")
                imageView.setOnClickListener{
                    val intent= Intent(context, InShopActivity::class.java)
                    intent.putExtra("shopname",m.shopName)
                    context.startActivity(intent)
                    //Log.e("--------------------------------","bind  "+ m.introImage.replace('*',':'))
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
            val view = LayoutInflater.from(parent.context) .inflate(R.layout.layout_shop_recycler_item, parent, false)
            //Log.e("--------------------------------","create")
            return viewHolder(view)
        }

        override fun onBindViewHolder(holder: viewHolder, position: Int) {
            val m=getItem(position)
            //holder.imageView.setImageResource(R.mipmap.message)
            if (m != null) {
                holder.bind(m,context)
            }
        }

        override fun getItemViewType(position: Int): Int {
            return super.getItemViewType(position)
        }
    }

}
