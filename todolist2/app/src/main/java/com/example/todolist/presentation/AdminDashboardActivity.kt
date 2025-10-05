package com.example.todolist.presentation

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.R
import com.example.todolist.database.DBHelper
import com.example.todolist.domain.User
import com.example.todolist.domain.UserManager

class AdminDashboardActivity : AppCompatActivity() {
    private lateinit var userManager: UserManager
    private lateinit var usersLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val db = DBHelper(this)
        userManager = UserManager(db)
        usersLayout = findViewById(R.id.usersLayout)

        loadUsers()

        val logoutBtn = findViewById<Button>(R.id.btnLogout)
        logoutBtn.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { _, _ ->
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
                finishAffinity()
                startActivity(Intent(this, LoginActivity::class.java))
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun loadUsers() {
        usersLayout.removeAllViews()
        val cursor = userManager.getAllUsers()

        if (cursor.moveToFirst()) {
            do {
                val username = cursor.getString(cursor.getColumnIndexOrThrow("username"))
                val password = cursor.getString(cursor.getColumnIndexOrThrow("password"))

                if (username == "admin") continue

                val userView = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(10, 10, 10, 10)
                }

                val userInfo = TextView(this).apply {
                    text = "$username | $password"
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                }

                val editBtn = Button(this).apply {
                    text = "Edit"
                    setOnClickListener { showEditDialog(username, password) }
                }

                val deleteBtn = Button(this).apply {
                    text = "Delete"
                    setOnClickListener { confirmDelete(username) }
                }

                userView.addView(userInfo)
                userView.addView(editBtn)
                userView.addView(deleteBtn)
                usersLayout.addView(userView)

            } while (cursor.moveToNext())
        } else {
            usersLayout.addView(TextView(this).apply { text = "No users found" })
        }

        cursor.close()
    }

    private fun showEditDialog(oldUsername: String, oldPassword: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_user, null)
        val etNewUsername = dialogView.findViewById<EditText>(R.id.etNewUsername)
        val etNewPassword = dialogView.findViewById<EditText>(R.id.etNewPassword)

        etNewUsername.setText(oldUsername)
        etNewPassword.setText(oldPassword)

        AlertDialog.Builder(this)
            .setTitle("Edit User")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newUser = User(
                    etNewUsername.text.toString().trim(),
                    etNewPassword.text.toString().trim()
                )

                if (newUser.username.isNotEmpty() && newUser.password.isNotEmpty()) {
                    if (userManager.updateUser(oldUsername, newUser)) {
                        Toast.makeText(this, "User updated", Toast.LENGTH_SHORT).show()
                        loadUsers()
                    } else {
                        Toast.makeText(this, "Failed to update user", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmDelete(username: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete User")
            .setMessage("Are you sure you want to delete '$username'?")
            .setPositiveButton("Yes") { _, _ ->
                if (userManager.deleteUser(username)) {
                    Toast.makeText(this, "User deleted", Toast.LENGTH_SHORT).show()
                    loadUsers()
                } else {
                    Toast.makeText(this, "Failed to delete user", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }
}
