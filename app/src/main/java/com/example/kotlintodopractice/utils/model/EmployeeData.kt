package com.example.kotlintodopractice.utils.model

data class EmployeeData(
    var employeeId: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var designation: String = ""
) {
    // No-argument constructor required for Firebase
    constructor() : this("", "", "", "")
}
