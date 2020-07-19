package com.bs

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bs.util.RecycleViewDivider
import com.bs.util.drawerMenuAdapter
import com.bs.util.recyclerViewlistener
import kotlinx.android.synthetic.main.layout_main_fragment.*
import java.util.*

class firstFragment() : Fragment() {
    lateinit var madapter : SimpleItemRecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_main_fragment,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //applicationContext.setContentView(R.layout.layout_main_fragment)

        showdrawer.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                drawer.openDrawer(GravityCompat.START)
                //madapter.keys.listIterator().forEach { s-> Log.e("*****************************      key   ",s) }
                //madapter.values.listIterator().forEach { s-> Log.e("*****************************   values   ",s) }
            }
        })

        drecycle.layoutManager = LinearLayoutManager(context)
        drecycle.addItemDecoration(RecycleViewDivider(requireContext(), LinearLayoutManager.HORIZONTAL,10,
                resources.getColor(R.color.colorAccent)))
        drecycle.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.colorAccent))
        drecycle.adapter = drawerMenuAdapter(mutableMapOf<Int,String>(R.mipmap.search to "search",R.mipmap.data to "data",R.mipmap.security to "security",R.mipmap.message to "message"))

        brecycle.layoutManager = LinearLayoutManager(context)
        brecycle.addItemDecoration(RecycleViewDivider(requireContext(), LinearLayoutManager.HORIZONTAL,10,resources.getColor(R.color.colorAccent)))
        this.madapter = SimpleItemRecyclerViewAdapter(mutableMapOf<String,String>("A" to "a","B" to "b","C" to "c","1" to "b","2" to "b","3" to "b","4" to "b","5" to "b","6" to "b","7" to "b","8" to "b","9" to "b","10" to "b","11" to "b","12" to "b","13" to "b"))
        val callback = recyclerViewlistener(madapter)
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(brecycle)
        brecycle.adapter =madapter

        val gestureDetector = GestureDetector(context,object : GestureDetector.OnGestureListener{
            override fun onShowPress(p0: MotionEvent?) {
                //Log.e("-----------------------------------------------------","showpress")
            }

            override fun onSingleTapUp(p0: MotionEvent?): Boolean {
                //Log.e("-----------------------------------------------------","singletap")
                return true
            }

            override fun onDown(p0: MotionEvent?): Boolean {
                //Log.e("-----------------------------------------------------","ondown")
                return true
            }

            override fun onFling(
                    p0: MotionEvent?,
                    p1: MotionEvent?,
                    p2: Float,
                    p3: Float
            ): Boolean {
                //Log.e("-----------------------------------------------------","onfling")
                return true
            }

            override fun onScroll(
                    p0: MotionEvent?,
                    p1: MotionEvent?,
                    p2: Float,
                    p3: Float
            ): Boolean {
                //Log.e("-----------------------------------------------------","onscroll")
                return true
            }

            override fun onLongPress(p0: MotionEvent?) {
                //Log.e("-----------------------------------------------------","onlongpress")
            }

        })
        drecycle.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener{
            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                //if(gestureDetector.onTouchEvent(e))
                //Log.e("----------------------","itemontouch")

                var item= rv.findChildViewUnder(e.x,e.y)?.let { rv.getChildViewHolder(it) }
                when(e.action){
                    MotionEvent.ACTION_DOWN-> {
                        if (item != null) {
                            madapter=SimpleItemRecyclerViewAdapter(mutableMapOf<String,String>("A" to "a","B" to "b","C" to "c","1" to "b","2" to "b","3" to "b","4" to "b","5" to "b"))
                            madapter.notifyDataSetChanged()
                            Log.e("----------------------", "item "+item.adapterPosition.toString())
                        }
                        Log.e("----------------------", "down")
                    }
                    MotionEvent.ACTION_MOVE-> {
                        Log.e("----------------------", "move")
                    }
                    MotionEvent.ACTION_UP  -> {
                        Log.e("----------------------","up  ")
                    }
                    MotionEvent.ACTION_SCROLL-> {
                        Log.e("----------------------", "scroll")
                    }
                }
            }

            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if(gestureDetector.onTouchEvent(e)){
                    Log.e("----------------------","InterceptTouch")
                    return true
                }
                else{
                    Log.e("----------------------","noInterceptTouch")
                    return false
                }
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })

        val drawerlistener : DrawerLayout.SimpleDrawerListener=object : DrawerLayout.SimpleDrawerListener(){
            override fun onDrawerStateChanged(newState: Int) {
                super.onDrawerStateChanged(newState)
                Log.e("----------------------","state change")
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                //Log.e("----------------------","slide")
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                Log.e("----------------------","close")
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                Log.e("----------------------","open")
            }
        }
        drawer.addDrawerListener(drawerlistener)

    }

    interface ItemTouchHelperAdapter {
        fun onItemMove(fromPosition: Int, toPosition: Int)
        fun onItemDissmiss(position: Int)
    }

    inner class SimpleItemRecyclerViewAdapter(map: MutableMap<String,String>) : RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() ,ItemTouchHelperAdapter {

        //private val mm=map
        internal val keys=map.keys.toMutableList()
        internal val values=map.values.toMutableList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context) .inflate(R.layout.layout_body_recyclerview_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.key.setText(keys[position])
            holder.va.setText(values[position])
            /*
            val call : ActionMode.Callback = object : ActionMode.Callback{
                override fun onActionItemClicked(p0: ActionMode?, p1: MenuItem?): Boolean {
                    if (p1 != null) {
                        when(p1.itemId){
                            R.id.item1->Log.e("----------------------","item1")
                            R.id.item2->Log.e("----------------------","item2")
                            R.id.item3->Log.e("----------------------","item3")
                        }
                    }

                    return false
                }

                override fun onCreateActionMode(p0: ActionMode?, p1: Menu?): Boolean {
                    val inflater = p0?.menuInflater
                    inflater?.inflate(R.menu.textselect,p1)
                    return true
                }

                override fun onPrepareActionMode(p0: ActionMode?, p1: Menu?): Boolean {
                    return false
                }

                override fun onDestroyActionMode(p0: ActionMode?) {

                }
            }
            holder.key.customSelectionActionModeCallback= call

             */
        }

        override fun getItemCount() = keys.count()

        override fun getItemViewType(position: Int): Int {
            return super.getItemViewType(position)
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var key =view.findViewById<EditText>(R.id.key)
            var va =view.findViewById<EditText>(R.id.value)
        }

        override fun onItemMove(fromPosition: Int, toPosition: Int) {
            Collections.swap(keys,fromPosition,toPosition)
            Collections.swap(values,fromPosition,toPosition)
            notifyItemMoved(fromPosition,toPosition)
        }

        override fun onItemDissmiss(position: Int) {
            keys.removeAt(position)
            values.removeAt(position)
            notifyItemRemoved(position)
        }

    }
}