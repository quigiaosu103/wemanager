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
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import java.io.IOException

class StudentInfo : AppCompatActivity() {
    private lateinit var btnEditStudent: ImageButton
    private lateinit var btnAdd:ImageButton
    private lateinit var btnSaveStudent: ImageButton
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
    private lateinit var certificateAdapter: CertificateAdapter;
    private lateinit var certificateRecycler: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_info)
        btnAdd = findViewById(R.id.btnAdd)
        btnBack = findViewById(R.id.btnBack)
        btnSave = findViewById(R.id.btnSave)
        inputName =  findViewById(R.id.txtName)
        inputAge = findViewById(R.id.inputAge)
        inputFaculty = findViewById(R.id.txtFacul)
        inputClas = findViewById(R.id.inputClass)
        inputPhone = findViewById(R.id.inputPhone)
        inputCreadits = findViewById(R.id.inputCreadits)
        inputId = findViewById(R.id.txtID)
        btnSaveStudent = findViewById(R.id.btnSaveSt)
        btnEditStudent = findViewById(R.id.btnEditStudent)
        labelWrapper = findViewById(R.id.labelWrapper)
        certificateRecycler = findViewById(R.id.certificateRecyclerView)
        certificateRecycler.layoutManager = LinearLayoutManager(this)

        val sharedPref = getSharedPreferences("storage_account", Context.MODE_PRIVATE)
        val role = sharedPref.getString("role", "null")
        if(role == "Employee") {
            btnAdd.isVisible = false
            btnEditStudent.isVisible = false
        }


        var isView = intent.getBooleanExtra("isView", false)
        var studentID = intent.getStringExtra("studentID")
        if(isView) {
            btnSave.isVisible = false
            getStudentInfo(studentID!!)
            btnSaveStudent.isVisible = false
            disableInput()
        }else {
            btnEditStudent.isVisible = false
            btnSaveStudent.isVisible = false
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

        btnEditStudent.setOnClickListener{
            v->
            btnSaveStudent.isVisible = true
            btnEditStudent.isVisible = false
            inputId.isEnabled = false
            inputAge.isEnabled = true
            inputName.isEnabled = true
            inputClas.isEnabled = true
            inputCreadits.isEnabled = true
            inputPhone.isEnabled = true
            inputFaculty.isEnabled = true
        }

        btnSaveStudent.setOnClickListener{
            v->
            var dataHandler = DataHandler()
            student.FullName = inputName.text.toString()
            student.PhoneNumber = inputPhone.text.toString()
            student.Deparment = inputFaculty.text.toString()
            student.Age = inputAge.text.toString().toInt()
            student.Class = inputClas.text.toString()
            student.Creadits = inputCreadits.text.toString().toInt()
           dataHandler.updateStudent(student)
            inputAge.isEnabled = false
            inputName.isEnabled = false
            inputClas.isEnabled = false
            inputCreadits.isEnabled = false
            inputPhone.isEnabled = false
            inputFaculty.isEnabled = false
            btnSaveStudent.isVisible = false
            btnEditStudent.isVisible = true
            Toast.makeText(this, "Update student successfully!", Toast.LENGTH_SHORT).show()
        }

        btnBack.setOnClickListener {
            v->
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btnAdd.setOnClickListener{
            v->
            certificateAdapter.createEmptyCreadit(studentID!!)
        }


    }

    fun getStudentInfo(stId:String) {
        var col = Firebase.firestore.collection("students")
        col.document(stId).get().addOnSuccessListener{
                document->
            if(document.data != null) {
                try {
                    var data  = document.data
                    var name = data?.get("fullName").toString()
                    var id = data?.get("id").toString()
                    var age = data?.get("age").toString().toInt()
                    var deparment = data?.get("deparment").toString()
                    var phone = data?.get("phoneNumber").toString()
                    var classof = data?.get("class").toString()
                    var creadits = data?.get("creadits").toString().toInt()
                    val certificatesList = data?.get("certificates") as ArrayList<HashMap<String, String>>
                    inputPhone.setText(phone)
                    inputAge.setText(age.toString())
                    inputId.setText(id)
                    inputFaculty.setText(deparment)
                    inputCreadits.setText(creadits.toString())
                    inputClas.setText(classof)
                    inputName.setText(name)
                    var newL = ArrayList<Certificate>()
                    for (ct in certificatesList) {
                        newL.add(Certificate(Id = ct.get("id")!!, Name = ct.get("name")!!, Content = ct.get("content")!!, Date = ct.get("date")!!, Signer = ct.get("signer")!!, studentId = ct.get("studentId")!!))
                    }
                    student = Student(Id = id!!, FullName = name!!, Age = age!!, Deparment = deparment!!, PhoneNumber = phone!!, Class = classof!!, Creadits = creadits!!, Certificates = newL)

                    certificateAdapter = CertificateAdapter(newL, this)
                    certificateRecycler.adapter = certificateAdapter
                }catch(e: IOException) {
                    e.printStackTrace()
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


}