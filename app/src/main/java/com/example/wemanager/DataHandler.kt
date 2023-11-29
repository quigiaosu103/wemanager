package com.example.wemanager

import android.util.Log
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DataHandler {
    public fun pushStudent(student: Student) {
        var collection = Firebase.firestore.collection("students")
        collection.document(student.Id).set(student)
    }

    public fun pushAccount(account: Account) {
        var col = Firebase.firestore.collection("accounts")
        col.document(account.UserName).set(account)
    }

    public fun pushHistory(time:String, username: String) {
        var col = Firebase.firestore.collection("accounts")
        col.document(username)
            .get()
            .addOnSuccessListener {
                document->
                var account: MutableMap<String, Any>? = document.data
                var b:ArrayList<String> = account?.get("history") as ArrayList<String>
                b.add(time)
                col.document(username).update("history", b)
            }
    }

    public fun removeAccount(username: String) {

        var col = Firebase.firestore.collection("accounts")
        col.document(username).delete()
            .addOnSuccessListener { Log.e("remove", "Romve successfully!")}
            .addOnFailureListener {Log.e("remove", "Romve failed!") }
    }

    public fun removeStudent(studentID: String) {

        var col = Firebase.firestore.collection("students")
        col.document(studentID).delete()
            .addOnSuccessListener { Log.e("remove", "Romve successfully!")}
            .addOnFailureListener {Log.e("remove", "Romve failed!") }
    }

    public fun updateAccount(account: Account) {
        Log.e("account", "${account}")

        val newData = hashMapOf(
            "fullName" to account.FullName,
            "age" to account.Age,
            "image" to account.Image,
            "role" to account.Role,
            "status" to account.Status

            // Add other fields you want to update
        )
        var col = Firebase.firestore.collection("accounts")
        col.document(account.UserName).set(newData, SetOptions.merge())
            .addOnSuccessListener { Log.e("remove", "Romve successfully!")}
            .addOnFailureListener {Log.e("remove", "Romve failed!") }
    }

    public fun updateStudent(student: Student) {

        val newData = hashMapOf(
            "age" to student.Age,
            "class" to student.Class,
            "creadits" to student.Creadits,
            "deparment" to student.Deparment,
            "fullName" to student.FullName,
            "phoneNumber" to student.PhoneNumber

            // Add other fields you want to update
        )
        var col = Firebase.firestore.collection("students")
        col.document(student.Id).set(newData, SetOptions.merge())
            .addOnSuccessListener { Log.e("remove", "Update successfully!")}
            .addOnFailureListener {Log.e("remove", "Update failed!") }
    }




}


