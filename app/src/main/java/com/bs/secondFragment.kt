package com.bs

import android.os.AsyncTask
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.bs.util.User
import com.bs.util.database
import kotlinx.android.synthetic.main.layout_fragment_second.*

class secondFragment : Fragment() {

    private lateinit var model: recyclerViewModel
    private lateinit var userList : MutableList<User>
    private lateinit var db : database
    private var mid : Int =0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        db = Room.databaseBuilder(
                requireContext(),
                database::class.java, "database"
        ).build()
        model = ViewModelProviders.of(this).get(recyclerViewModel::class.java)
        userList = mutableListOf()

        val root = User(mid, "root")
        userList.add(root)
        // Create the observer which updates the UI.
        val nameObserver = Observer<MutableList<User>> { newUser ->
            // Update the UI, in this case, a TextView.
            //userList.add(newUser)
            Log.e("*************************",""+newUser.toString())
            showtv.append("observe change \n")
        }

        model.userList.observe(viewLifecycleOwner,nameObserver)

        return inflater.inflate(R.layout.layout_fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        showtv.movementMethod = ScrollingMovementMethod.getInstance()

        this.butw.setOnClickListener {
            mid++
            val user = User(mid, "abc")

            Thread(Runnable { db.userDao().insertAll(user) }).start()
            showtv.append("insert user "+user.toString()+"\n")
            model.userList.value=userList
        }

        this.butr.setOnClickListener{
            asyncTask().execute(0)
        }

        this.butd.setOnClickListener{
            Thread(Runnable { db.userDao().delete(db.userDao().getAll())}).start()
            showtv.append("delete all users \n")
            userList.clear()
            model.userList.value=userList
        }

        recycle.layoutManager = LinearLayoutManager(context)
        recycle.adapter = recyclerViewAdapter(userList)

        super.onViewCreated(view, savedInstanceState)
    }

    inner class asyncTask : AsyncTask<Int,Int,String>(){
        override fun doInBackground(vararg p0: Int?): String {
            //Log.e("","")
            return "read user "+db.userDao().getAll().toString()+"\n"
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: String?) {
            showtv.append(result)
        }

        override fun onCancelled(result: String?) {
            super.onCancelled(result)
        }

        override fun onCancelled() {
            super.onCancelled()
        }

        override fun onPreExecute() {
            super.onPreExecute()
        }
    }
}

class recyclerViewAdapter(list: MutableList<User>) : RecyclerView.Adapter<recyclerViewAdapter.ViewHolder>(){
    val list = list

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val editText = view.findViewById<EditText>(R.id.second_et)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context) .inflate(R.layout.layout_second_fragment_recyclerview_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.editText.setText(list.get(position).userid.toString() + "" + list.get(position).username)
    }
}

class recyclerViewModel() : ViewModel() {
    val userList : MutableLiveData<MutableList<User>> by lazy {
        MutableLiveData<MutableList<User>>()
    }

    fun getUser(): LiveData<MutableList<User>?>? {
        return userList
    }

}