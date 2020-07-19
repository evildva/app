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
import androidx.lifecycle.observe
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bs.R
import com.bumptech.glide.Glide
import com.example.guilongyuangaoyue.myapplication.util.sharedPreferences
import kotlinx.android.synthetic.main.layout_realshop.*
import okhttp3.*
import java.io.IOException

class store : Fragment() {

    val shopid = arguments?.getString("shopname")


    open fun getShopId() : String?{
        return shopid
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_realshop,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Glide.with(this).asBitmap().load("http://47.115.36.237:8000/shopimage/p3.jpg").into(store_image)

        val viewModel = ViewModelProviders.of(this).get(pageViewModel::class.java)
        val adapter = context?.let { pageListAdapter(it) }
        store_recyclerview.layoutManager= LinearLayoutManager(context)
        store_recyclerview.adapter=adapter
        viewModel.pageList.observe(viewLifecycleOwner, Observer { t ->
            adapter?.submitList(t)
        })

        super.onViewCreated(view, savedInstanceState)
    }

}

data class vegetable(val shopId : Int, val vegName : String, val introImage : String, val more : String)
//data class shoplist(val shopList: MutableList<shop>)

class pageDataSource: PageKeyedDataSource<Int, vegetable>(){

    fun getShopList(json : String) : MutableList<vegetable>{
        val shop= mutableListOf<vegetable>()

        for(str in json.replace("[","").replace("]","").split("},")){
            val item=str.replace("{","").replace("}","").split(",")
            val vl= mutableListOf<String>()
            for (s in item){
                val value=s.split(":")[1]
                vl.add(value)
            }
            shop.add(vegetable(vl[0].toInt(),vl[1],vl[2],vl[3]))
        }

        return shop
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, vegetable>) {
        val client = OkHttpClient()
        var shopid="abc"
        val request = Request.Builder().url("http://47.115.36.237:8000/shop/store/${shopid}").build()
        Log.e("--------------------shopid  ",shopid.toString())
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("--------------------------------","net error")
            }

            override fun onResponse(call: Call, response: Response) {
                val result = response.body?.string()
                val shopList= getShopList(result!!)
                //Log.e("--------------------------------","init "+shopList.toString())
                callback.onResult(shopList,null,1)
            }
        })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, vegetable>) {
       var shopid="abc"
        val client = OkHttpClient()
        val request = Request.Builder().url("http://47.115.36.237:8000/shop/store/${shopid}").build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("--------------------------------","net error")
            }

            override fun onResponse(call: Call, response: Response) {
                val result= response.body?.string()
                val shopList=getShopList(result!!)
                //Log.e("-------------------------------- "+params.key,"  after  "+shopList.toString())
                callback.onResult(shopList,params.key+1)
            }
        })

    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, vegetable>) {
        //callback.onResult(getShopList(params.key),params.key-1)
        Log.e("--------------------------------","before")
    }

}

class pageDataFactory : androidx.paging.DataSource.Factory<Int, vegetable>(){
    override fun create(): androidx.paging.DataSource<Int, vegetable> {
        return pageDataSource()
    }

}

class pageViewModel : ViewModel(){
    val pageList= LivePagedListBuilder(pageDataFactory(), PagedList.Config.Builder().setEnablePlaceholders(true).setPageSize(3).build()).build()
}

class pageListAdapter(val context: Context) : PagedListAdapter<vegetable, pageListAdapter.viewHolder>(
    diffCallback
){
    companion object{
        val diffCallback=object : DiffUtil.ItemCallback<vegetable>(){
            override fun areItemsTheSame(oldItem: vegetable, newItem: vegetable): Boolean {
                return oldItem.shopId==newItem.shopId
            }

            override fun areContentsTheSame(oldItem: vegetable, newItem: vegetable): Boolean {
                return oldItem.vegName==newItem.vegName
            }

        }
    }

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imageView=itemView.findViewById<ImageView>(R.id.shop_recycler_item_image)
        val textView=itemView.findViewById<TextView>(R.id.shop_recycler_item_tv)

        fun bind(m : vegetable, context: Context){
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
                Log.e("-----------------------------url ", m.introImage.replace('*',':').replace("\"",""))
                val request = Request.Builder().url(m.vegName.replace('*',':').replace("\"","")).get().build() //默认为get请求
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
            textView.text=m.introImage.replace("\"","")+"                      price "+m.shopId
            imageView.setOnClickListener{

                Log.e("--------------------------------","bind  "+ m.introImage.replace('*',':'))
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