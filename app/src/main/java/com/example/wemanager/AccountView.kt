package com.example.wemanager

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.api.ResourceDescriptor.History
import com.google.firebase.database.FirebaseDatabase
import java.io.IOException

class AccountView : AppCompatActivity() {
    private lateinit var txtUserName: EditText
    private lateinit var account: Account
    private lateinit var txtAge: EditText
    private lateinit var txtName: EditText
    private lateinit var txtPhone: EditText
    private lateinit var txtRole: EditText
    private lateinit var txtStatus: EditText
    private lateinit var imgAvatar: ImageView
    private lateinit var layout: RecyclerView
    private lateinit var btnBack: ImageButton
    private lateinit var btnEdit: ImageButton
    private lateinit var btnSave: ImageButton
    private lateinit var btnImageChange: ImageButton
    private lateinit var historyAdapter : HistoryAdapter

    private lateinit var imageUri: Uri
    private var isEditing = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_view)
        txtUserName = findViewById(R.id.inputUsername)
        txtName = findViewById(R.id.txtName)
        txtPhone = findViewById(R.id.inputPhone)
        txtAge = findViewById(R.id.inputAge)
        txtRole = findViewById(R.id.inputRole)
        txtStatus = findViewById(R.id.inputStatus)
        imgAvatar = findViewById(R.id.imgAvatar)
        layout = findViewById(R.id.historyRecyclerView)
        layout.layoutManager = LinearLayoutManager(this)
        btnBack = findViewById(R.id.btnBack)
        btnSave = findViewById(R.id.btnSave)
        btnEdit = findViewById(R.id.btnEditInfo)
        btnImageChange = findViewById(R.id.btnChangeImage)
        btnSave.isVisible = false
        btnImageChange.isVisible = false

        var username = intent.getStringExtra("username").toString()
        getAccountByUserName(username)
        btnBack.setOnClickListener{
            v->
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btnEdit.setOnClickListener{
            v->
            enableEditable()

        }

        btnSave.setOnClickListener{
                v->

                account.FullName = txtName.text.toString()
                account.Age = txtAge.text.toString().toInt()
                account.Role = txtRole.text.toString()
                account.PhoneNumber = txtPhone.text.toString()
                account.Status = txtStatus.text.toString()
                var dataHandler = DataHandler()
                dataHandler.pushAccount(account)
                disabledEditable()


        }

        btnImageChange.setOnClickListener{
            v->
            selectImage()
        }



    }



    public fun getAccountByUserName(username: String){
        var ref = FirebaseDatabase.getInstance().getReference("accounts")
        var historyList:ArrayList<String> = ArrayList()
        ref.child(username).get().addOnCompleteListener{
                task->
            if(task.isSuccessful) {
                if (task.result.exists()) {
                    try {
                        var dataSnapshot = task.result
                        var age = dataSnapshot.child("age").getValue(Int::class.java)!!
                        var fullName = dataSnapshot.child("fullName").getValue(String::class.java)
                        var hashPass = dataSnapshot.child("hashPassword").getValue(String::class.java)
                        var image = dataSnapshot.child("image").getValue(String::class.java)
                        var phone = dataSnapshot.child("phoneNumber").getValue(String::class.java)
                        var role = dataSnapshot.child("role").getValue(String::class.java)
                        var status = dataSnapshot.child("status").getValue(String::class.java)
                        var userName = dataSnapshot.child("userName").getValue(String::class.java)
                        var historySnapshot = dataSnapshot.child("history")
                        if(historySnapshot.exists()) {
                            for (history in historySnapshot.children) {
                                historyList.add(history.key!!)
                            }
                        }
                        Log.i("result", "out")
                        account = Account(Age=age, UserName = userName!!, FullName = fullName!!, HashPassword = hashPass!!, Image = image!!, PhoneNumber = phone!!, Role = role!!, Status = status!!, History = historyList)
                        //using data here
                        txtName.setText(account.FullName)
                        Log.i("result", "out1")
                        txtUserName.setText(account.UserName)
                        Log.i("result", "out2")
                        txtRole.setText(account.Role)
                        Log.i("result", "out3")
                        txtAge.setText(account.Age.toString())
                        Log.i("result", "out4")
                        txtPhone.setText(account.PhoneNumber)
                        Log.i("result", "out5")
                        txtStatus.setText(account.Status)
                        Log.i("result", "out6")

                        var imageHandler = ImageHandler()
                        Log.i("result", "out7")
                        imageHandler.getImage(account.Image, imgAvatar)
                        Log.i("result", "out8")
                        historyAdapter = HistoryAdapter(historyList)
                        layout.adapter = historyAdapter
                        Toast.makeText(this, "lenght "+ historyAdapter.itemCount, Toast.LENGTH_SHORT).show()
                    }catch(e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

        }

    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && data != null && data.data != null) {
            imageUri = data.data!!
            imgAvatar.setImageURI(imageUri)
            var imageHandler = ImageHandler()
            var fileName = imageHandler.uploadImage(this, imageUri)
            account.Image = fileName
        }
    }

    fun enableEditable() {
        btnImageChange.isVisible = true
        btnSave.isVisible = true
        btnEdit.isVisible = false
        txtAge.isEnabled = true
        txtAge.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#cccccc"))
        txtPhone.isEnabled = true
        txtPhone.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#cccccc"))
        txtName.isEnabled = true
        txtName.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#cccccc"))
        txtRole.isEnabled = true
        txtRole.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#cccccc"))
        txtStatus.isEnabled = true
        txtStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#cccccc"))

    }

    fun disabledEditable() {
        btnSave.isVisible = false
        btnEdit.isVisible = true
        btnImageChange.isVisible = false
        txtAge.isEnabled = false
        txtAge.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#ffffff"))
        txtPhone.isEnabled = false
        txtPhone.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#ffffff"))
        txtName.isEnabled = false
        txtName.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#ffffff"))
        txtRole.isEnabled = false
        txtRole.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#ffffff"))
        txtStatus.isEnabled = false
        txtStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#ffffff"))
    }
}