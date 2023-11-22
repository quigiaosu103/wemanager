package com.example.wemanager

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.auth.User

class NewAccount : AppCompatActivity() {
    private lateinit var txtUserName: EditText
    private lateinit var txtAge: EditText
    private lateinit var txtName: EditText
    private lateinit var txtPhone: EditText
    private lateinit var txtRole: EditText
    private lateinit var txtStatus: EditText
    private lateinit var txtPass: EditText
    private lateinit var imgAvatar: ImageView
    private lateinit var btnBack: ImageButton
    private lateinit var btnSave: ImageButton
    private lateinit var btnImageChange: ImageButton
    private lateinit var imageUri: Uri
    private var img = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_account)
        txtUserName = findViewById(R.id.inputUsername)
        txtName = findViewById(R.id.txtName)
        txtPass = findViewById(R.id.inputPass)
        txtPhone = findViewById(R.id.inputPhone)
        txtAge = findViewById(R.id.inputAge)
        txtRole = findViewById(R.id.inputRole)
        txtStatus = findViewById(R.id.inputStatus)
        imgAvatar = findViewById(R.id.imgAvatar)
        btnBack = findViewById(R.id.btnBack)
        btnSave = findViewById(R.id.btnSave)
        btnImageChange = findViewById(R.id.btnChangeImage)

        btnBack.setOnClickListener{
                v->
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        btnSave.setOnClickListener{
                v->

            var name = txtName.text.toString()
            var age = txtAge.text.toString().toInt()
            var role = txtRole.text.toString()
            var phone = txtPhone.text.toString()
            var status = txtStatus.text.toString()
            var pass = txtPass.text.toString()
            var username = txtUserName.text.toString()
            var dataHandler = DataHandler()
            dataHandler.pushAccount(Account(UserName = username, HashPassword = pass, FullName = name, Age = age, Role = role, Status = status, PhoneNumber = phone, History = ArrayList(emptyList<String>()), Image = img))
            Toast.makeText(this, "Create account successfullly!", Toast.LENGTH_SHORT).show()
        }

        btnImageChange.setOnClickListener{
                v->
                selectImage()
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
            img = fileName
        }
    }
}