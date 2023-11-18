package com.example.wemanager

import android.content.ContentValues.TAG
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.IOException

class DataHandler {
    public fun pushStudent(student: Student) {
        var database = FirebaseDatabase.getInstance()
        var ref = database.getReference("Student")
        ref.child(student.Id).setValue(student)
    }

    public fun pushAccount(account: Account) {
        var database = FirebaseDatabase.getInstance()
        var ref = database.getReference("accounts")
        ref.child(account.UserName).setValue(account)
    }

    public fun getStudents():ArrayList<Student> {
        var studentsList = ArrayList<Student>()
        val studentsListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists())
                    for(data in dataSnapshot.children) {
                        var name = data.child("FullName").getValue().toString()
                        var id = data.child("Id").getValue().toString()
                        var age = data.child("Age").getValue(Int::class.java)
                        var deparment = data.child("Deparment").getValue().toString()
                        var phone = data.child("PhoneNumber").getValue().toString()
                        var classof = data.child("Class").getValue().toString()
                        var creadits = data.child("Creadits").getValue(Int::class.java)

                        val certificatesList = ArrayList<Certificate>()
                        val certificatesSnapshot = data.child("Certificates")
                        if(certificatesSnapshot.exists()) {
                            for (certificateSnapshot in certificatesSnapshot.children) {
                                val name = certificateSnapshot.child("Name").getValue(String::class.java)
                                if (name == null) {
                                    Log.e("null", "name null")
                                }
                                val content = certificateSnapshot.child("Content").getValue(String::class.java)
                                if (content == null) {
                                    Log.e("null", "content null")
                                }else {
                                    Log.e("null", content.toString())
                                }
                                val signer = certificateSnapshot.child("Signer").getValue(String::class.java)
                                if (signer == null) {
                                    Log.e("null", "signer null")
                                }
                                val date = certificateSnapshot.child("Date").getValue(String::class.java)
                                if (date == null) {
                                    Log.e("null", "date null")
                                }
                                val id = certificateSnapshot.child("Id").getValue(String::class.java)
                                if (id == null) {
                                    Log.e("null", "id null")
                                }
                                val certificate = Certificate(Name=name!!, Content = content!!, Date =  date!!, Signer =  signer!!, Id = id!!)
                                certificatesList.add(certificate)
                            }
                        }

                        Log.e("null", "id: "+id)
                        Log.e("null", "name: "+name)
                        Log.e("null", "age: "+age)
                        Log.e("null", "deparment: "+deparment)
                        Log.e("null", "phone: "+phone)
                        Log.e("null", "phone: "+phone)
                        Log.e("null", "class: "+classof)
                        Log.e("null", "creadits: "+creadits)

                        val student = Student(Id = id!!, FullName = name!!, Age = age!!, Deparment = deparment!!, PhoneNumber = phone!!, Class = classof!!, Creadits = creadits!!, Certificates = certificatesList)
                        studentsList.add(student)
                    }

                //using data here
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        var ref = FirebaseDatabase.getInstance().getReference("Student")
        ref.addValueEventListener(studentsListener)
        return studentsList
    }

    public fun getStudentById(student: Student){
        var ref = FirebaseDatabase.getInstance().getReference("Student")
        var FullName = "name"
        var Id = "id"
        var Age = 0
        var Deparment = "depar"
        var PhoneNumber = "phone"
        var Class = "class"
        var Creadits = 0
        var Certificates:ArrayList<Certificate> = ArrayList()
        ref.child(student.Id).get().addOnCompleteListener{
            task->
            if(task.isSuccessful) {
                if (task.result.exists()) {
                    try {
                        var dataSnapshot = task.result
                        FullName = dataSnapshot.child("FullName").getValue().toString()
                        Log.i("result", FullName)
                        Id = dataSnapshot.child("Id").getValue().toString()
                        Age = dataSnapshot.child("Age").getValue(Int::class.java)!!
                        Deparment = dataSnapshot.child("Deparment").getValue().toString()
                        PhoneNumber = dataSnapshot.child("PhoneNumber").getValue().toString()
                        Class = dataSnapshot.child("Class").getValue().toString()
                        Creadits = dataSnapshot.child("Creadits").getValue(Int::class.java)!!
                        var certificatesSnapshot = dataSnapshot.child("Certificates")
                        if(certificatesSnapshot.exists()) {
                            for (certificateSnapshot in certificatesSnapshot.children) {
                                val name = certificateSnapshot.child("Name").getValue(String::class.java)
                                val content = certificateSnapshot.child("Content").getValue(String::class.java)
                                val signer = certificateSnapshot.child("Signer").getValue(String::class.java)
                                val date = certificateSnapshot.child("Date").getValue(String::class.java)
                                val id = certificateSnapshot.child("Id").getValue(String::class.java)
                                Certificates.add(Certificate(Name=name!!, Content = content!!, Date =  date!!, Signer =  signer!!, Id = id!!))
                            }
                        }
                        student.FullName = FullName
                        Log.i("result", "out")
                        //using data here
                    }catch(e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

        }
        Log.i("result", "end")

    }


}