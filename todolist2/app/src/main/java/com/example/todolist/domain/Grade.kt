package com.example.todolist.domain

data class Grade(
    val id: String = "",           // Firestore document ID
    val username: String = "",
    val course: String = "",
    val type: String = "",
    val status: String = "",
    val dueDate: String = ""
)
