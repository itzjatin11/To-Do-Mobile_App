package com.example.todolist.domain

import android.database.Cursor
import com.example.todolist.database.DBHelper

class UserManager(private val dbHelper: DBHelper) {

    fun getAllUsers(): Cursor {
        return dbHelper.getAllUsers()
    }

    fun updateUser(oldUsername: String, newUser: User): Boolean {
        return dbHelper.updateUser(oldUsername, newUser.username, newUser.password)
    }

    fun deleteUser(username: String): Boolean {
        return dbHelper.deleteUser(username)
    }
}
