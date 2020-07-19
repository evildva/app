package com.example.guilongyuangaoyue.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bs.R
import com.bs.login.logInActivity
import com.example.guilongyuangaoyue.myapplication.util.sharedPreferences
import kotlinx.android.synthetic.main.layout_fragment_fouth.*

class fouthFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_fragment_fouth,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var user =""
        activity?.applicationContext?.let { user=sharedPreferences(it).getString("user") }
        if(user!=null){
            for ( str in user.split("#")){
                var item= str.split("&")
                //four_username.text=item[1]
                Log.e("--------------------------",str)
            }
        }

        four_username.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                if(four_username.text==null){
                    startActivity(Intent(activity?.applicationContext,
                        logInActivity::class.java))
                }
            }

        })

        super.onViewCreated(view, savedInstanceState)
    }
}