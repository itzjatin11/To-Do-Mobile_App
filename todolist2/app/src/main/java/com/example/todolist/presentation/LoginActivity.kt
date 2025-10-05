package com.example.todolist.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.R
import com.example.todolist.database.DBHelper
import com.example.todolist.domain.AuthManager

class LoginActivity : AppCompatActivity() {
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)

        val dbHelper = DBHelper(this)
        authManager = AuthManager(dbHelper)

        val username = findViewById<EditText>(R.id.etUsername)
        val password = findViewById<EditText>(R.id.etPassword)
        val loginBtn = findViewById<Button>(R.id.btnLogin)
        val registerTxt = findViewById<TextView>(R.id.tvRegister)

        loginBtn.setOnClickListener {
            val user = username.text.toString().trim()
            val pass = password.text.toString().trim()

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            when (authManager.login(user, pass)) {
                is AuthManager.LoginResult.Admin -> {
                    Toast.makeText(this, "Admin Login Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AdminDashboardActivity::class.java))
                    finish()
                }
                is AuthManager.LoginResult.User -> {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("username", user)
                    startActivity(intent)
                    finish()
                }
                is AuthManager.LoginResult.Invalid -> {
                    Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                }
            }
        }

        registerTxt.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
