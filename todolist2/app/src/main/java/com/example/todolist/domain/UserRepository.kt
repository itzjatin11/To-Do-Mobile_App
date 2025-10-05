package com.example.todolist.domain

import com.example.todolist.database.DBHelper

class UserRepository(private val dbHelper: DBHelper) {

    fun isAdmin(username: String, password: String): Boolean {
        return username.equals("admin", ignoreCase = true) && password == "admin123"
    }

    fun validateUser(username: String, password: String): Boolean {
        return dbHelper.checkUser(username, password)
    }
}
