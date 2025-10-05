package com.example.todolist.domain

data class RssItem(
    val title: String?,
    val link: String?,
    val pubDate: String?,
    val description: String?
) {
    // Provide a sanitized description for UI display
    fun getSanitizedDescription(): String {
        return description?.replace(Regex("<[^>]+>"), "") ?: ""
    }
}