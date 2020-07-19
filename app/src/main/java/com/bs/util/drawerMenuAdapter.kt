package com.bs.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bs.R

class drawerMenuAdapter(map: MutableMap<Int,String>/*,array: Array<RecyclerView.OnItemTouchListener>*/) : RecyclerView.Adapter<drawerMenuAdapter.ViewHolder>(){

    val keys = map.keys
    val values = map.values

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): drawerMenuAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context) .inflate(R.layout.layout_drawer_menu_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return keys.count()
    }

    override fun onBindViewHolder(holder: drawerMenuAdapter.ViewHolder, position: Int) {
        holder.drawer.setImageResource(keys.toTypedArray()[position])
        holder.dtext.text=values.toTypedArray()[position]
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var drawer=view.findViewById<ImageView>(R.id.drawer_image)
        var dtext=view.findViewById<TextView>(R.id.drawer_text)
    }
}