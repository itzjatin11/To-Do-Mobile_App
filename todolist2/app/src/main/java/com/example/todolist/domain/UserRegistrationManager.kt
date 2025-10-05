package com.example.todolist.domain

import com.example.todolist.database.DBHelper

class UserRegistrationManager(private val dbHelper: DBHelper) {

    fun registerUser(username: String, password: String): Boolean {
        return dbHelper.insertUser(username, password)
    }
}
