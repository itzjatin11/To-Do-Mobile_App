package com.example.todolist.domain

data class MeetingData(
    val id: Int = 0,
    val title: String,
    val date: String, // Expected format: yyyy-MM-dd (e.g., 2025-07-28)
    val time: String, // Expected format: HH:mm (e.g., 14:30)
    val purpose: String?,
    val attendees: String?,
    val username: String
)