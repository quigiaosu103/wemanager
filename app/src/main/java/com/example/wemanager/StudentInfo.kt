package com.example.wemanager

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import java.io.IOException

class StudentInfo : AppCompatActivity() {
    private lateinit var btnBack: ImageButton
    private lateinit var btnSave: ImageButton
    private lateinit var inputName: EditText
    private lateinit var inputAge: EditText
    private lateinit var inputFaculty: EditText
    private lateinit var inputClas: EditText
    private lateinit var inputPhone: EditText
    private lateinit var inputCreadits: EditText
    private lateinit var inputId: EditText
    private lateinit var labelWrapper: LinearLayout
    private lateinit var student: Student
    private lateinit var certificateAdapter: CertificateAdapter
    private lateinit var certificateRecycler: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_info)
        btnBack = findViewById(R.id.btnBack)
        btnSave = findViewById(R.id.btnSave)
        inputName =  findViewById(R.id.txtName)
        inputAge = findViewById(R.id.inputAge)
        inputFaculty = findViewById(R.id.txtFacul)
        inputClas = findViewById(R.id.inputClass)
        inputPhone = findViewById(R.id.inputPhone)
        inputCreadits = findViewById(R.id.inputCreadits)
        inputId = findViewById(R.id.txtID)
        labelWrapper = findViewById(R.id.labelWrapper)
        certificateRecycler = findViewById(R.id.certificateRecyclerView)
        certificateRecycler.layoutManager = LinearLayoutManager(this)

        var isView = intent.getBooleanExtra("isView", false)
        var studentID = intent.getStringExtra("studentID")
        if(isView) {
            btnSave.isVisible = false
            getStudentInfo(studentID!!)
            disableInput()
        }else {
            labelWrapper.isVisible = false
        }

        btnSave.setOnClickListener{
            v->
            var name = inputName.text.toString()
            var age = inputAge.text.toString().toInt()
            var faculty = inputFaculty.text.toString()
            var classof = inputClas.text.toString()
            var phone = inputPhone.text.toString()
            var creadits = inputCreadits.text.toString().toInt()
            var id = inputId.text.toString()
            var student = Student(Id = id, FullName = name, PhoneNumber = phone, Age = age, Deparment = faculty, Class = classof, Creadits = creadits, Certificates = emptyList())
            var dataHandler = DataHandler()
            dataHandler.pushStudent(student)
            startActivity(Intent(this, MainActivity::class.java))

        }

    }

    fun getStudentInfo(stId:String) {
        var ref = FirebaseDatabase.getInstance().getReference("Student")
        Log.e("result", stId)
        ref.child(stId).get().addOnCompleteListener{
                task->
            if(task.isSuccessful) {
                if (task.result.exists()) {
                    try {

                        var data = task.result
                        var name = data.child("fullName").getValue().toString()
                        var id = data.child("id").getValue().toString()
                        var age = data.child("age").getValue(Int::class.java)
                        var deparment = data.child("deparment").getValue().toString()
                        var phone = data.child("phoneNumber").getValue().toString()
                        var classof = data.child("class").getValue().toString()
                        var creadits = data.child("creadits").getValue(Int::class.java)
                        Log.e("result",name)
                        val certificatesList = ArrayList<Certificate>()
                        val certificatesSnapshot = data.child("certificates")
                        if(certificatesSnapshot.exists()) {
                            for (certificateSnapshot in certificatesSnapshot.children) {
                                val name = certificateSnapshot.child("name").getValue(String::class.java)
                                val content = certificateSnapshot.child("content").getValue(String::class.java)
                                val signer = certificateSnapshot.child("signer").getValue(String::class.java)
                                val date = certificateSnapshot.child("date").getValue(String::class.java)
                                val id = certificateSnapshot.child("id").getValue(String::class.java)
                                val certificate = Certificate(Name=name!!, Content = content!!, Date =  date!!, Signer =  signer!!, Id = id!!)
                                certificatesList.add(certificate)
                            }
                        }
                            student = Student(Id = id!!, FullName = name!!, Age = age!!, Deparment = deparment!!, PhoneNumber = phone!!, Class = classof!!, Creadits = creadits!!, Certificates = certificatesList)
                            inputPhone.setText(student.PhoneNumber)
                            Log.e("result",student.PhoneNumber)
                            inputAge.setText(student.Age.toString())
                            inputId.setText(student.Id)
                            inputFaculty.setText(student.Deparment)
                            inputCreadits.setText(student.Creadits.toString())
                            inputClas.setText(student.Class)
                            inputName.setText(student.FullName)
                            certificateRecycler.adapter = CertificateAdapter(certificatesList)

                    }catch(e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

        }

    }

    fun disableInput() {
        inputName.isEnabled = false
        inputId.isEnabled = false
        inputCreadits.isEnabled = false
        inputClas.isEnabled = false
        inputFaculty.isEnabled = false
        inputAge.isEnabled = false
        inputPhone.isEnabled = false

    }

    fun enableInput() {

    }


//    private fun selectImage() {
//        val intent = Intent()
//        intent.type = "image/*"
//        intent.action = Intent.ACTION_GET_CONTENT
//
//        startActivityForResult(intent, 100)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 100 && data != null && data.data != null) {
//            imageUri = data.data!!
//            image.setImageURI(imageUri)
//            var imageHandler = ImageHandler()
//            var fileName = imageHandler.uploadImage(this, imageUri)
//            Toast.makeText(this, fileName, Toast.LENGTH_SHORT).show()
//        }
//    }
}