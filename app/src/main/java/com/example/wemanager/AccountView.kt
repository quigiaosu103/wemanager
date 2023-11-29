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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.IOException

class AccountView : AppCompatActivity() {
    private lateinit var txtUserName: EditText
    private lateinit var currAccount: Account
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
    private lateinit var historyAdapter: HistoryAdapter
    private var isView = false
    private lateinit var imageUri: Uri
    private var isEmployy = false
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
        isView = intent.getBooleanExtra("isView", false)
        if(isView) {
            layout.isVisible= false
            txtRole.isEnabled = false
            txtStatus.isEnabled = false
        }
        var username = intent.getStringExtra("username").toString()
        var role = intent.getStringExtra("role").toString()
        if(role != null) {
            if(role == "Employee") {
                isEmployy= true
            }
        }
        getAccountByUserName(username)
        btnBack.setOnClickListener { v ->
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btnEdit.setOnClickListener { v ->
            enableEditable()

        }

        btnSave.setOnClickListener { v ->
            currAccount.FullName = txtName.text.toString()
            currAccount.Age = txtAge.text.toString().toInt()
            currAccount.Role = txtRole.text.toString()
            currAccount.PhoneNumber = txtPhone.text.toString()
            currAccount.Status = txtStatus.text.toString()

            var dataHandler = DataHandler()
            dataHandler.updateAccount(currAccount)
            disabledEditable()


        }

        btnImageChange.setOnClickListener { v ->
            selectImage()
        }


    }





    public fun getAccountByUserName(username: String){
        var col = Firebase.firestore.collection("accounts")
        var historyList:ArrayList<String> = ArrayList()
        col.document(username).get().addOnSuccessListener{
                document->
            if(document.data != null) {
                    try {
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
                        currAccount = Account(Age=age, UserName = userName!!, FullName = fullName!!, HashPassword = hashPass!!, Image = image!!, PhoneNumber = phone!!, Role = role!!, Status = status!!, History = historyList)
                        //using data here
                        txtName.setText(currAccount.FullName)
                        txtUserName.setText(currAccount.UserName)
                        txtRole.setText(currAccount.Role)
                        txtAge.setText(currAccount.Age.toString())
                        txtPhone.setText(currAccount.PhoneNumber)
                        txtStatus.setText(currAccount.Status)
                        var imageHandler = ImageHandler()
                        imageHandler.getImage(currAccount.Image, imgAvatar)
                        historyAdapter = HistoryAdapter(historyList)
                        layout.adapter = historyAdapter
                    }catch(e: IOException) {
                        e.printStackTrace()
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
            currAccount.Image = fileName
        }
    }

    fun enableEditable() {
        btnImageChange.isVisible = true
        btnSave.isVisible = true
        btnEdit.isVisible = false
         if(!isView) {
            txtRole.isEnabled = true
            txtRole.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#cccccc"))
            txtStatus.isEnabled = true
            txtStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#cccccc"))
        }

        if(!isEmployy) {
            txtAge.isEnabled = true
            txtAge.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#cccccc"))
            txtPhone.isEnabled = true
            txtPhone.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#cccccc"))
            txtName.isEnabled = true
            txtName.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#cccccc"))
        }

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