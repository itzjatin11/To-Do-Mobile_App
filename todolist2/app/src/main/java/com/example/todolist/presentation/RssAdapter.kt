package com.example.todolist.presentation

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.domain.RssItem

class RssAdapter(private val rssItems: List<RssItem>) :
    RecyclerView.Adapter<RssAdapter.RssViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RssViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rss_article, parent, false)
        return RssViewHolder(view)
    }

    override fun onBindViewHolder(holder: RssViewHolder, position: Int) {
        val item = rssItems[position]
        holder.title.text = item.title ?: "No Title"
        holder.date.text = item.pubDate ?: "No Date"
        holder.description.text = item.getSanitizedDescription()

        holder.itemView.setOnClickListener {
            val context = it.context
            val urlString = item.link?.trim()
            if (urlString.isNullOrEmpty()) {
                Toast.makeText(context, "No URL available for this item.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val validUrl = when {
                    urlString.startsWith("http://") || urlString.startsWith("https://") -> urlString
                    else -> "https://$urlString"
                }
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(validUrl))
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "No browser found to open link.", Toast.LENGTH_SHORT).show()
                Log.e("RssAdapter", "Failed to open link: $urlString", e)
            } catch (e: Exception) {
                Toast.makeText(context, "Invalid URL: $urlString", Toast.LENGTH_SHORT).show()
                Log.e("RssAdapter", "Unexpected error opening link: $urlString", e)
            }
        }
    }

    override fun getItemCount(): Int = rssItems.size

    class RssViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.titleText)
        val date: TextView = itemView.findViewById(R.id.dateText)
        val description: TextView = itemView.findViewById(R.id.descriptionText)
    }
}