package com.example.wemanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Register : AppCompatActivity() {
    private var btnBack: Button?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        btnBack = findViewById(R.id.btnToLogin)
        btnBack!!.setOnClickListener{
            v->
                var intent = Intent(this, Login::class.java)
                startActivity(intent)

        }

    }
}