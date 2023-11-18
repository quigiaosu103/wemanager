package com.example.wemanager

data class Student(var Id: String, var FullName: String, var Age: Int, var Creadits: Int, var Deparment: String, var PhoneNumber: String, var Certificates: List<Certificate>, var Class: String) {
}