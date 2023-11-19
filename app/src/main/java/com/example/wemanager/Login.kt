package com.example.wemanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase

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
            var ref = FirebaseDatabase.getInstance().getReference("accounts")
            ref.child(inputUserName!!.text.toString()).get().addOnCompleteListener{
                task ->
                if(task.isSuccessful){
                    if(task.result.exists()) {
                        var dataSnapshot = task.result
                        var pass = dataSnapshot.child("hashPassword").getValue(String::class.java)
                        var role = dataSnapshot.child("role").getValue(String::class.java)
                        Log.e("login", "request pass: "+pass)
                        if(pass == inputPass!!.text.toString()) {
                            if(role!="Locked") {
                                var intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            }
                        }else {
                            Toast.makeText(this, "User name or password is not correct", Toast.LENGTH_SHORT).show()
                        }
                    }else {
                        Toast.makeText(this, "User name or password is not correct", Toast.LENGTH_SHORT).show()
                    }
                }else {
                    Toast.makeText(this, "failed 1", Toast.LENGTH_SHORT).show()
                }

            }
        }


    }
}