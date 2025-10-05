package com.example.todolist.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.todolist.R
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var tvRandomQuote: TextView
    private var username: String? = null

    private val quotes = listOf(
        "A good teacher can inspire hope, ignite the imagination, and instill a love of learning. – Brad Henry",
        "Teaching is the one profession that creates all other professions. – Unknown",
        "The art of teaching is the art of assisting discovery. – Mark Van Doren",
        "Teachers affect eternity; no one can tell where their influence stops. – Henry Adams",
        "Education is not the filling of a pail, but the lighting of a fire. – William Butler Yeats",
        "It is the supreme art of the teacher to awaken joy in creative expression and knowledge. – Albert Einstein",
        "To teach is to touch a life forever. – Unknown",
        "The influence of a great teacher can never be erased. – Unknown"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        drawerLayout = findViewById(R.id.drawer_layout)
        tvRandomQuote = findViewById(R.id.tvRandomQuote)
        username = intent.getStringExtra("username")
        val safeUsername = username ?: "User"

        // Greeting and random quote
        findViewById<TextView>(R.id.tvGreeting).text = "Hello $safeUsername"
        tvRandomQuote.text = quotes.random()

        // Menu drawer toggle
        findViewById<ImageView>(R.id.menu).setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        // Logout
        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Category buttons
        findViewById<LinearLayout>(R.id.btnGrading).setOnClickListener {
            startActivityWithUsername(GradingActivity::class.java)
        }

        findViewById<LinearLayout>(R.id.btnLectures).setOnClickListener {
            startActivityWithUsername(LecturesActivity::class.java)
        }

        findViewById<LinearLayout>(R.id.btnMeetings).setOnClickListener {
            startActivityWithUsername(MeetingsActivity::class.java)
        }

        findViewById<LinearLayout>(R.id.btnAddTask).setOnClickListener {
            startActivityWithUsername(AddTaskActivity::class.java)
        }

        findViewById<LinearLayout>(R.id.btnViewTask).setOnClickListener {
            startActivityWithUsername(ViewTaskActivity::class.java)
        }

        findViewById<LinearLayout>(R.id.btnRssFeed).setOnClickListener {
            startActivity(Intent(this, RssActivity::class.java))
        }

        // Social media links
        findViewById<ImageButton>(R.id.btnFacebook).setOnClickListener {
            openUrl("https://www.facebook.com/StudyAIS/")
        }

        findViewById<ImageButton>(R.id.btnInstagram).setOnClickListener {
            openUrl("https://www.instagram.com/study_ais/")
        }

        findViewById<ImageButton>(R.id.btnLinkedIn).setOnClickListener {
            openUrl("https://www.linkedin.com/school/auckland-institute-of-studies/")
        }

        // Navigation drawer menu items
        val navView = findViewById<NavigationView>(R.id.nav_view)
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_about -> showInfoDialog(
                    "About Auckland Institute of Studies",
                    "Welcome to Auckland Institute of Studies (AIS) – a vibrant education hub in New Zealand.\n\n" +
                            "This app helps teachers manage their daily tasks like lectures, grading, and meetings with ease.\n\n" +
                            "Need to update your login details? Just reach out to the admin team for quick assistance."
                )
                R.id.nav_contact -> showContactOptions()
                R.id.nav_map -> startActivity(Intent(this, MapActivity::class.java))
                R.id.nav_ais_website -> openUrl("https://www.ais.ac.nz")
                R.id.nav_play_video -> startActivity(Intent(this, VideoActivity::class.java))
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun startActivityWithUsername(cls: Class<*>) {
        val intent = Intent(this, cls)
        intent.putExtra("username", username)
        startActivity(intent)
    }

    private fun showInfoDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showContactOptions() {
        val options = arrayOf("Call Support", "Email Support")

        AlertDialog.Builder(this)
            .setTitle("Contact Us")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:0800788392")
                        }
                        startActivity(dialIntent)
                    }
                    1 -> {
                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:")
                            putExtra(Intent.EXTRA_EMAIL, arrayOf("karwenc@ais.ac.nz"))
                            putExtra(Intent.EXTRA_SUBJECT, "Support Request")
                            putExtra(Intent.EXTRA_TEXT, "Hi, I need help with...")
                        }
                        try {
                            startActivity(Intent.createChooser(emailIntent, "Choose email client"))
                        } catch (e: Exception) {
                            Toast.makeText(this, "No email client installed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "No browser found or invalid link", Toast.LENGTH_SHORT).show()
        }
    }
}
