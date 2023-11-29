package com.example.wemanager

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.apache.commons.csv.CSVFormat
import java.io.InputStream
import java.io.OutputStream

class ExportData : AppCompatActivity() {
    private var students: List<Student> = emptyList()
    private lateinit var btnImportSt: Button
    private lateinit var btnImportCer: Button
    private lateinit var btnExportSt: Button
    private lateinit var btnExportCer: Button
    private lateinit var btnBack: ImageButton
    private var exportType: String = "students"
    private var importType: String = "students"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_export_data)

        btnExportCer = findViewById(R.id.btnExportCertificates)
        btnExportSt = findViewById(R.id.btnExportStudents)
        btnImportSt = findViewById(R.id.btnImportStudents)
        btnImportCer = findViewById(R.id.btnImportCertificates)
        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }
        val pickFile = registerForActivityResult(ActivityResultContracts.GetContent()){
            if (it != null) {
                val contentResolver = contentResolver
                val inputStream = contentResolver.openInputStream(it)
                var handler = DataHandler()
                if(importType == "students") {
                    inputStream?.use { stream ->
                        var lists = emptyList<Student>()
                        lists = readCsv(stream)
                        for(st in lists) {
                            handler.pushStudent(st)
                        }
                    }
                    Toast.makeText(this, "Import Students successfully", Toast.LENGTH_SHORT).show()
                }else {
                    inputStream?.use { stream ->
                        var lists = emptyList<Certificate>()
                        lists = readCerCsv(stream)
                        for(st in lists) {
                            handler.updateCer(st)
                        }
                    }
                    Toast.makeText(this, "Import Certificates successfully", Toast.LENGTH_SHORT).show()
                }


            }
        }


        btnExportCer.setOnClickListener{
            v->
            exportType = "certificates"
            createFile()
        }

        btnExportSt.setOnClickListener{
                v->
            exportType = "students"
            createFile()
        }

        btnImportSt.setOnClickListener{
                v->
            importType = "students"
             pickFile.launch("*/*")

        }

        btnImportCer.setOnClickListener{
                v->
            importType = "certificates"
            pickFile.launch("*/*")
        }
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (requestCode == 123
            && resultCode == Activity.RESULT_OK) {
            resultData?.data?.also { uri ->
                val outputStream: OutputStream? = applicationContext.contentResolver.openOutputStream(uri)

                outputStream?.apply {
                    val writer = bufferedWriter()
                    if(exportType == "students") {
                        writer.write("Id,FullName,Class,Faculty,Age,PhoneNumber,Creadits")
                    }else {
                        writer.write("StudentID,StudentName,CertificateName,Content,Date,Signer")
                    }
                    writer.newLine()
                    var db = Firebase.firestore
                    db.collection("students")
                        .addSnapshotListener { snapshots, e ->
                            if (e != null) {
                                Log.w(ContentValues.TAG, "listen:error", e)
                                return@addSnapshotListener
                            }
                            for (document in snapshots!!.documents) {
                                var st: MutableMap<String, Any>? = document.data
                                if(exportType == "students") {
                                    writer.write("${st?.get("id").toString()}," +
                                            "${st?.get("fullName").toString()}," +
                                            "${st?.get("class").toString()}," +
                                            "${st?.get("deparment").toString()}," +
                                            "${st?.get("age").toString().toInt()}," +
                                            "${st?.get("phoneNumber").toString()}," +
                                            "${st?.get("creadits").toString().toInt()}")
                                    writer.newLine()
                                }else {
                                    val certificatesList = st?.get("certificates") as ArrayList<HashMap<String, String>>
                                    for (cer in certificatesList) {
                                        writer.write("${st?.get("id").toString()}," +
                                                "${st?.get("fullName").toString()}," +
                                                "${cer.get("name")}," +
                                                "${cer.get("content")}," +
                                                "${cer.get("date")}," +
                                                "${cer.get("signer")}")
                                        writer.newLine()
                                    }
                                }

                            }
                            writer.flush()
                            outputStream?.close()
                        }


                }


            }
        }
    }



    private fun createFile() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/csv"
            putExtra(Intent.EXTRA_TITLE, "users.csv")

        }
        startActivityForResult(intent, 123)
    }
    fun readCerCsv(inputStream: InputStream): List<Certificate> {
        return CSVFormat.Builder.create(CSVFormat.DEFAULT).apply {
            setIgnoreSurroundingSpaces(true)
        }.build().parse(inputStream.reader()).drop(1) // Dropping the header
            .map {
                Certificate(
                    Id = it[0],
                    Name = it[1],
                    Content = it[2],
                    Date = it[3],
                    Signer = it[4],
                    studentId = it[5]
                );
            }
    }

    fun readCsv(inputStream: InputStream): List<Student> {
        return CSVFormat.Builder.create(CSVFormat.DEFAULT).apply {
            setIgnoreSurroundingSpaces(true)
        }.build().parse(inputStream.reader()).drop(1) // Dropping the header
            .map {
                Student(
                    Id = it[0],
                    FullName = it[1],
                    Class = it[2],
                    Deparment = it[3],
                    Age = it[4].toInt(),
                    PhoneNumber = it[5],
                    Creadits = it[5].toInt(),
                    Certificates = emptyList(),
                );
            }
    }
}