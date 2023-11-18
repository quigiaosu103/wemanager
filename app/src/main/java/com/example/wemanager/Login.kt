package com.example.wemanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class Login : AppCompatActivity() {
    private var btnSubmit: Button?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btnSubmit = findViewById(R.id.submit)

        btnSubmit!!.setOnClickListener{
            v->
                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
        }


    }
}