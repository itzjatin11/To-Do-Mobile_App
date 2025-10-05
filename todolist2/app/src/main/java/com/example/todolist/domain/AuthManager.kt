package com.example.todolist.domain

import com.example.todolist.database.DBHelper

class AuthManager(private val dbHelper: DBHelper) {

    fun login(user: String, pass: String): LoginResult {
        return when {
            isAdmin(user, pass) -> LoginResult.Admin
            checkUser(user, pass) -> LoginResult.User
            else -> LoginResult.Invalid
        }
    }

    private fun isAdmin(user: String, pass: String) = user == "admin" && pass == "admin123"

    private fun checkUser(user: String, pass: String) = dbHelper.checkUser(user, pass)

    sealed class LoginResult {
        object Admin : LoginResult()
        object User : LoginResult()
        object Invalid : LoginResult()
    }
}
