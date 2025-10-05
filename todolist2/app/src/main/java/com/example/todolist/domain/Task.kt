package com.example.todolist.domain

data class Task(
    val id: Int = 0,
    val username: String,
    val title: String,
    val description: String,
    val dueDate: String,
    val dueTime: String,
    val fileUri: String?,
    val status: Boolean = false,
    val reminderHour: Int? = null,
    val reminderMinute: Int? = null
)
