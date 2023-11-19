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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
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
import java.text.Collator
import java.util.Locale
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private var btn: Button?=null
    private lateinit var database: FirebaseDatabase
    private lateinit var ref: DatabaseReference
    private lateinit var layout: RecyclerView
    private lateinit var adapter: StudentAdapter
    private  var toolbar: Toolbar?=null
    private var adapterData =  ArrayList<Student>()
    private var isSelecting = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        layout = findViewById(R.id.mainRecyclerView)
        layout.layoutManager = LinearLayoutManager(this)
        adapterData.add(Student(Id="52100996", FullName = "Qui", Deparment = "IT", Age = 20, Class = "1231", Creadits = 90, PhoneNumber = "3e3434", Certificates = emptyList()))

        adapter = StudentAdapter(adapterData)

        layout.adapter = adapter
        getStudents()
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
        val searchItem = menu?.findItem(R.id.search)
        val searchView = searchItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                handleSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.add -> {
//


                var dataHandler = DataHandler()

                var id = Random.nextInt(10000,99999)
                dataHandler.pushStudent(Student(id.toString(), "Vo Nguyen Phu Qui", Age = 20, Class= "283921", Deparment = "IT", PhoneNumber = "d903", Creadits = 100, Certificates = emptyList()))
//                dataHandler.pushAccount(Account(FullName = "Phu Qui", Age=20, PhoneNumber = "07391239", Role = "Admin", Status = "Normal",  Image = "http://dasdsa.jpg", HashPassword = "dajsdna", UserName = "Quigiaosu103", History = ArrayList<String>()))
//                var intent = Intent(this, StudentInfo::class.java)
//                startActivity(intent)
                return true
            }
            R.id.sortAge-> {

                adapterData= ArrayList(adapterData.sortedBy { it.Age })
                adapter = StudentAdapter(adapterData)
                layout.adapter = adapter
                return true
            }

            R.id.sortByDe -> {
                adapterData = ArrayList(adapterData.sortedBy { it.Deparment })
                adapter = StudentAdapter(adapterData)
                layout.adapter = adapter
                return true
            }
            R.id.sortByName -> {
                var collator = Collator.getInstance(Locale("vi", "VN"))
                adapterData = ArrayList(adapterData.sortedWith(compareBy(collator) {it.FullName}))
                adapter = StudentAdapter(adapterData)
                layout.adapter = adapter
                return true
            }
            R.id.sortId -> {
                adapterData = ArrayList(adapterData.sortedBy { it.Id })
                adapter = StudentAdapter(adapterData)
                layout.adapter = adapter
                return true
            }
            R.id.removeMany -> {
                isSelecting = true
                adapter.isSelecting = true
                invalidateOptionsMenu()
                return true
            }
            R.id.btnCancle -> {
                isSelecting = false
                adapter.isSelecting = false
                adapter.removeIdList.clear()
                adapter = StudentAdapter(adapterData)
                layout.adapter = adapter
                invalidateOptionsMenu()
                return true
            }
            R.id.btnDel -> {
                confirmDelete(1)
                invalidateOptionsMenu()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    public fun getStudents(){

        val studentsListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                adapterData = ArrayList<Student>()
                if (dataSnapshot.exists())
                    for(data in dataSnapshot.children) {
                        var name = data.child("fullName").getValue().toString()
                        var id = data.child("id").getValue().toString()
                        var age = data.child("age").getValue(Int::class.java)
                        var deparment = data.child("deparment").getValue().toString()
                        var phone = data.child("phoneNumber").getValue().toString()
                        var classof = data.child("class").getValue().toString()
                        var creadits = data.child("creadits").getValue(Int::class.java)

                        val certificatesList = ArrayList<Certificate>()
                        val certificatesSnapshot = data.child("certificates")
                        if(certificatesSnapshot.exists()) {
                            for (certificateSnapshot in certificatesSnapshot.children) {
                                val name = certificateSnapshot.child("name").getValue(String::class.java)
                                if (name == null) {
                                    Log.e("null", "name null")
                                }
                                val content = certificateSnapshot.child("content").getValue(String::class.java)
                                if (content == null) {
                                    Log.e("null", "content null")
                                }else {
                                    Log.e("null", content.toString())
                                }
                                val signer = certificateSnapshot.child("signer").getValue(String::class.java)
                                if (signer == null) {
                                    Log.e("null", "signer null")
                                }
                                val date = certificateSnapshot.child("date").getValue(String::class.java)
                                if (date == null) {
                                    Log.e("null", "date null")
                                }
                                val id = certificateSnapshot.child("id").getValue(String::class.java)
                                if (id == null) {
                                    Log.e("null", "id null")
                                }
                                val certificate = Certificate(Name=name!!, Content = content!!, Date =  date!!, Signer =  signer!!, Id = id!!)
                                certificatesList.add(certificate)
                            }
                        }

                        val student = Student(Id = id!!, FullName = name!!, Age = age!!, Deparment = deparment!!, PhoneNumber = phone!!, Class = classof!!, Creadits = creadits!!, Certificates = certificatesList)
                        adapterData.add(student)
                    }
//                var sortList = ArrayList(studentsList.sortedBy { it.Age })
                adapter = StudentAdapter(adapterData)
                layout.adapter = adapter
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        var ref = FirebaseDatabase.getInstance().getReference("Student")
        ref.addValueEventListener(studentsListener)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {

        for (i in 0 until menu.size()) {
            menu.getItem(i).isVisible = !isSelecting
        }


        menu.findItem(R.id.btnDel).isVisible = isSelecting
        menu.findItem(R.id.btnCancle).isVisible = isSelecting

        return super.onPrepareOptionsMenu(menu)
    }

    fun confirmDelete(index: Int) {
        var builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Confirm Delete Students")
            .setTitle("Alert")
            .setPositiveButton("DELETE") {
                    dialog, which ->
                        if(1 == index) {
                            deleteStudentsFB()
                            adapter.removeSelected()
                            isSelecting = false
                            adapter.isSelecting = false
                            invalidateOptionsMenu()
                            getStudents()
                        }else if (2==index) {
                            adapter.removeSelected()
                        }
            }
            .setNegativeButton("CANCLE") {
                dialog, which ->
            }
        val dialog: AlertDialog = builder.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                v->
            dialog.cancel()
        }
    }

    fun deleteStudentsFB() {
        var database = FirebaseDatabase.getInstance()
        var ref = database.getReference("Student")
        var idList = adapter.removeIdList
        idList.forEach({
            it->
            ref.child(it).removeValue().addOnCompleteListener{
                it ->
                Toast.makeText(this, "Romve successfully!", Toast.LENGTH_SHORT).show()
            }
                .addOnFailureListener{
                    it ->
                    Toast.makeText(this, "Romve Failed!", Toast.LENGTH_SHORT).show()
                }
        })
    }

    fun handleSearch(searchValue: String) {
        var newAdapterData = ArrayList(adapterData.filter { it.FullName.contains(searchValue, ignoreCase = true) ||it.Id.contains(searchValue, ignoreCase = true) ||it.Class.startsWith(searchValue, ignoreCase = true) ||it.Deparment.startsWith(searchValue, ignoreCase = true)})
        adapter = StudentAdapter(newAdapterData)
        layout.adapter = adapter
    }
}