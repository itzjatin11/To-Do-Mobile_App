package com.example.todolist.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.domain.RssParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class RssActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private val rssUrl = "https://www.nasa.gov/rss/dyn/breaking_news.rss"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rss)

        recyclerView = findViewById(R.id.rssRecyclerView)
        progressBar = findViewById(R.id.rssProgressBar)

        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchRssFeed()
    }

    private fun fetchRssFeed() {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            var connection: HttpURLConnection? = null
            try {
                Log.d("RSS", "Fetching RSS feed from: $rssUrl")

                val url = URL(rssUrl)
                connection = url.openConnection() as HttpURLConnection
                connection.apply {
                    connectTimeout = 10000 // Increased timeout
                    readTimeout = 10000
                    requestMethod = "GET"
                    setRequestProperty("User-Agent", "Mozilla/5.0 (Android; Mobile; rv:68.0)")
                    doInput = true
                    connect()
                }

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    throw Exception("HTTP error code: ${connection.responseCode}")
                }

                val inputStream = connection.inputStream
                val rssItems = RssParser.parse(inputStream)
                inputStream.close()

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    if (rssItems.isNotEmpty()) {
                        recyclerView.adapter = RssAdapter(rssItems)
                    } else {
                        Toast.makeText(this@RssActivity, "No RSS items found.", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: java.net.SocketTimeoutException) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@RssActivity, "Connection timed out. Please try again.", Toast.LENGTH_LONG).show()
                }
                Log.e("RSS", "Timeout error", e)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@RssActivity, "Failed to load RSS: ${e.localizedMessage ?: "Unknown error"}", Toast.LENGTH_LONG).show()
                }
                Log.e("RSS", "Error loading RSS", e)
            } finally {
                connection?.disconnect()
            }
        }
    }
}