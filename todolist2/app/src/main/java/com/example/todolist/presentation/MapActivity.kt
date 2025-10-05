package com.example.todolist.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.example.todolist.R

class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val btnOpenMap = findViewById<Button>(R.id.btnOpenGoogleMap)

        btnOpenMap.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:0,0?q=Auckland+Institute+of+Studies,+St+Helens+Campus")

            // Try to launch with Google Maps if available
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            try {
                startActivity(mapIntent)
            } catch (e: Exception) {
                // Fallback to any available map app
                val unrestrictedIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                try {
                    startActivity(unrestrictedIntent)
                } catch (e: Exception) {
                    Toast.makeText(this, "No Maps application available", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}
