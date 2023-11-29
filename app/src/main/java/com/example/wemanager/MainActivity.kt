package com.example.wemanager

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.Collator
import java.util.Locale
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    private var btn: Button?=null
    private lateinit var database: FirebaseDatabase
    private lateinit var ref: DatabaseReference
    private lateinit var layout: RecyclerView
    private lateinit var accountAdapter: AccountAdapter
    private lateinit var adapter: StudentAdapter
    private  var toolbar: Toolbar?=null
    private var adapterData =  ArrayList<Student>()
    private var accountAdapterData = ArrayList<Account>()
    private var isSelecting = false
    private var isUserMagerment = false
    private val context = this
    private lateinit var btnInfo: ImageButton
    private lateinit var btnHome: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        layout = findViewById(R.id.mainRecyclerView)
        btnInfo = findViewById(R.id.btnInfo)
        btnHome = findViewById(R.id.btnHome)
        btnInfo.setOnClickListener{
            v->
            val sharedPref = getSharedPreferences("storage_account", Context.MODE_PRIVATE)
            val usname = sharedPref.getString("username", "null")
            val role = sharedPref.getString("role", "Employee")
            var intent = Intent(this, AccountView::class.java)
            intent.putExtra("isView", true)
            intent.putExtra("username", usname)
            intent.putExtra("role", role)
            startActivity(intent)
        }
        layout.layoutManager = LinearLayoutManager(this)
        adapterData.add(Student(Id="52100996", FullName = "Qui", Deparment = "IT", Age = 20, Class = "1231", Creadits = 90, PhoneNumber = "3e3434", Certificates = emptyList()))
        adapter = StudentAdapter(adapterData, context,applicationContext)
        layout.adapter = adapter
        getStudents()
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

                if(isUserMagerment) {
                    var intent = Intent(this, NewAccount::class.java)
                    startActivity(intent)
                }else {
                    var intent = Intent(this, StudentInfo::class.java)
                    intent.putExtra("isView", false)
                    startActivity(intent)
                }
                return true
            }
            R.id.sortAge-> {

                adapterData= ArrayList(adapterData.sortedBy { it.Age })
                adapter = StudentAdapter(adapterData,context, applicationContext)
                layout.adapter = adapter
                return true
            }

            R.id.sortByDe -> {
                adapterData = ArrayList(adapterData.sortedBy { it.Deparment })
                adapter = StudentAdapter(adapterData,context, applicationContext)
                layout.adapter = adapter
                return true
            }
            R.id.sortByName -> {
                var collator = Collator.getInstance(Locale("vi", "VN"))
                adapterData = ArrayList(adapterData.sortedWith(compareBy(collator) {it.FullName}))
                adapter = StudentAdapter(adapterData, context,applicationContext)
                layout.adapter = adapter
                return true
            }
            R.id.sortId -> {
                adapterData = ArrayList(adapterData.sortedBy { it.Id })
                adapter = StudentAdapter(adapterData, context, applicationContext)
                layout.adapter = adapter
                return true
            }
            R.id.btnCancle -> {
                isSelecting = false
                adapter.isSelecting = false
                adapter.removeIdList.clear()
                adapter = StudentAdapter(adapterData, context, applicationContext)
                layout.adapter = adapter
                invalidateOptionsMenu()
                return true
            }
            R.id.btnDel -> {
                confirmDelete(1)
                invalidateOptionsMenu()
            }
            R.id.exit -> {
                startActivity(Intent(this, Login::class.java))
            }
            R.id.userManager -> {
                isUserMagerment = true
                getAccounts()
                invalidateOptionsMenu()
            }
            R.id.studentManager -> {
                isUserMagerment = false
                getStudents()
                invalidateOptionsMenu()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    public fun getStudents(){
        var db = Firebase.firestore
        db.collection("students")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "listen:error", e)
                    return@addSnapshotListener
                }
                adapterData = ArrayList<Student>()
                var historyList: ArrayList<String> = ArrayList(emptyList<String>())
                for (document in snapshots!!.documents) {
                    var certificatesList: ArrayList<Certificate> = ArrayList(emptyList())
                    var st: MutableMap<String, Any>? = document.data
                    var name = st?.get("fullName").toString()
                    var id = st?.get("id").toString()
                    var age = st?.get("age").toString().toInt()
                    var deparment = st?.get("deparment").toString()
                    var phone = st?.get("phoneNumber").toString()
                    var classof = st?.get("class").toString()
                    var creadits = st?.get("creadits").toString().toInt()
                    certificatesList = st?.get("certificates") as ArrayList<Certificate>
                    Log.e("Cer", ("${certificatesList}"))
                    val student = Student(Id = id!!, FullName = name!!, Age = age!!, Deparment = deparment!!, PhoneNumber = phone!!, Class = classof!!, Creadits = creadits!!, Certificates = certificatesList)
                    adapterData.add(student)
                }
                adapter = StudentAdapter(adapterData, context,  applicationContext)
                layout.adapter = adapter
            }


    }


    public fun getAccounts() {
        var db = Firebase.firestore
        db.collection("accounts")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "listen:error", e)
                    return@addSnapshotListener
                }
                accountAdapterData = ArrayList<Account>()

                var historyList: ArrayList<String> = ArrayList(emptyList<String>())
                for (document in snapshots!!.documents) {
                    var account: MutableMap<String, Any>? = document.data
                    var age = account?.get("age").toString().toInt()
                    var fullName = account?.get("fullName").toString()
                    var hashPass = account?.get("hashPassword").toString()
                    var image = account?.get("image").toString()
                    var phone = account?.get("phoneNumber").toString()
                    var role = account?.get("role").toString()
                    var status = account?.get("status").toString()
                    var userName = account?.get("userName").toString()
                    historyList = account?.get("history") as ArrayList<String>

                    Log.i("result", "out")
                    var newAccount = Account(Age=age, UserName = userName!!, FullName = fullName!!, HashPassword = hashPass!!, Image = image!!, PhoneNumber = phone!!, Role = role!!, Status = status!!, History = historyList)
                    accountAdapterData.add(newAccount)

                }
                accountAdapter = AccountAdapter(accountAdapterData, context)
                adapter.clear()
                layout.adapter = accountAdapter
            }



    }


    override fun onPrepareOptionsMenu(menu: Menu): Boolean {

        for (i in 0 until menu.size()) {
            menu.getItem(i).isVisible = !isSelecting
        }
        menu.findItem(R.id.studentManager).isVisible = isUserMagerment
        menu.findItem(R.id.search).isVisible = !isUserMagerment

        val sharedPref = getSharedPreferences("storage_account", Context.MODE_PRIVATE)
        val role = sharedPref.getString("role", "null")
        if(role != "Admin") {
            menu!!.findItem(R.id.userManager).isVisible = false
        }else {
            menu!!.findItem(R.id.userManager).isVisible = !isUserMagerment
            menu.findItem(R.id.sortId).isVisible = !isUserMagerment
            menu.findItem(R.id.sortAge).isVisible = !isUserMagerment
            menu.findItem(R.id.sortByName).isVisible = !isUserMagerment
            menu.findItem(R.id.sortByDe).isVisible = !isUserMagerment
        }

        if(role =="Employee") {
            menu!!.findItem(R.id.add).isVisible = false
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
        var col = Firebase.firestore
        var idList = adapter.removeIdList
        idList.forEach { it ->
            col.document(it).delete()
                .addOnSuccessListener { Log.e("remove", "Romve successfully!") }
                .addOnFailureListener { Log.e("remove", "Romve failed!") }
        }
    }


    fun handleSearch(searchValue: String) {
        var newAdapterData = ArrayList(adapterData.filter { it.FullName.contains(searchValue, ignoreCase = true) ||it.Id.contains(searchValue, ignoreCase = true) ||it.Class.startsWith(searchValue, ignoreCase = true) ||it.Deparment.startsWith(searchValue, ignoreCase = true)})
        adapter = StudentAdapter(newAdapterData, context, applicationContext)
        layout.adapter = adapter
    }

    fun getShareRefData() {
        val sharedPref = getSharedPreferences("storage_account", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "null")
        val role =  sharedPref.getString("role", "null")
        Toast.makeText(this, "username: $username, role: $role", Toast.LENGTH_SHORT).show()
    }

}