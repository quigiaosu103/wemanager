package com.example.wemanager

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import androidx.appcompat.widget.Toolbar
class MainActivity : AppCompatActivity() {
    private var btn: Button?=null
    private lateinit var database: FirebaseDatabase
    private lateinit var ref: DatabaseReference
    private lateinit var layout: RecyclerView
    private lateinit var adapter: StudentAdapter
    private  var toolbar: Toolbar?=null
    private var adapterData =  ArrayList<Student>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        layout = findViewById(R.id.mainRecyclerView)
        layout.layoutManager = LinearLayoutManager(this)
        adapterData.add(Student(Id="52100996", FullName = "Qui", Deparment = "IT", Age = 20, Class = "1231", Creadits = 90, PhoneNumber = "3e3434", Certificates = emptyList()))

        adapter = StudentAdapter(adapterData)

        layout.adapter = adapter
//        btn = findViewById(R.id.btn)
//        btn!!.setOnClickListener{
//            v->
//Account(FullName = "Phu Qui", Age=20, PhoneNumber = "07391239", Role = "Admin", Status = "Normal",  Image = "http://dasdsa.jpg", HashPassword = "dajsdna")
//            database = FirebaseDatabase.getInstance()
//            ref = database.getReference("accounts")
//            var account =
//            ref.child("quivo111").setValue(account)
//        }
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.add -> {
//


                var dataHandler = DataHandler()
                var list: ArrayList<Student> = dataHandler.getStudents()
                Log.i("Result", "size: "+list.size)




//                dataHandler.pushAccount(Account(FullName = "Phu Qui", Age=20, PhoneNumber = "07391239", Role = "Admin", Status = "Normal",  Image = "http://dasdsa.jpg", HashPassword = "dajsdna", UserName = "Quigiaosu103"))
//                var intent = Intent(this, StudentInfo::class.java)
//                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}