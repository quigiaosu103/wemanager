package com.example.wemanager

data class Account(val UserName: String,
                   var Age: Int,
                   var FullName: String,
                   var Image: String,
                   var HashPassword: String,
                   var PhoneNumber: String,
                   var Role: String,
                   var Status: String,
                   var History: ArrayList<String>) {
}