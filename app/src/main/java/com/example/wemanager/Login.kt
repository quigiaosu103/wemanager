package com.example.wemanager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class Login : AppCompatActivity() {
    private var btnSubmit: Button?=null
    private var inputUserName: TextView?=null
    private var inputPass: TextView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btnSubmit = findViewById(R.id.submit)
        inputPass = findViewById(R.id.inputPassword)
        inputUserName = findViewById(R.id.inputUserName)
        btnSubmit!!.setOnClickListener{
            v->
            var col = Firebase.firestore.collection("accounts")
            col.document(inputUserName!!.text.toString()).get().addOnSuccessListener{
                    document->
                if(document.data != null) {
                        var dataSnapshot: MutableMap<String, Any>? = document.data
                        var pass = dataSnapshot!!.get("hashPassword").toString()
                        var role = dataSnapshot!!.get("role").toString()
                        var status = dataSnapshot!!.get("status").toString()
                        var usName = inputUserName!!.text.toString()
                        Log.e("login", "request pass: "+pass)
                        if(pass == inputPass!!.text.toString()) {
                            if(status!="Locked") {
                                val sharedPref = getSharedPreferences("storage_account", Context.MODE_PRIVATE)
                                val editor = sharedPref.edit()
                                editor.putString("username", usName)
                                editor.putString("role", role)
                                editor.apply()
                                var dataHandler = DataHandler()

                                val currentDateTime = LocalDateTime.now()
                                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                val date: String = currentDateTime.format(formatter)
                                dataHandler.pushHistory(time = date, usName)
                                var intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            }else {
                                Toast.makeText(this, "Your account have been locked!", Toast.LENGTH_SHORT).show()
                            }
                        }else {
                            Toast.makeText(this, "User name or password is not correct", Toast.LENGTH_SHORT).show()
                        }

                }else {
                    Toast.makeText(this, "User name or password is not correct", Toast.LENGTH_SHORT).show()
                }

            }
        }


    }
}