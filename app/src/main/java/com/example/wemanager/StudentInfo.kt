package com.example.wemanager

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast

class StudentInfo : AppCompatActivity() {
    private lateinit var btnUpload: Button
    private lateinit var image: ImageView
    private lateinit var imageUri: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_info)
        image = findViewById(R.id.image)
        btnUpload = findViewById(R.id.btnUploadImage)
        var imgHandler = ImageHandler()
        imgHandler.getImage("2023_11_17_21_57_05", image)
        btnUpload.setOnClickListener{
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
            image.setImageURI(imageUri)
            var imageHandler = ImageHandler()
            var fileName = imageHandler.uploadImage(this, imageUri)
            Toast.makeText(this, fileName, Toast.LENGTH_SHORT).show()
        }
    }
}