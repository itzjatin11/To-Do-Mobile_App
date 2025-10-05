package com.example.todolist.domain

import com.example.todolist.database.DBHelper

class TaskManager(private val dbHelper: DBHelper) {

    fun addTask(task: Task): Boolean {
        // Future: Add validations or business rules here
        return dbHelper.insertTask(task)
    }

    fun getTasksByUser(username: String): List<Task> {
        val tasks = mutableListOf<Task>()
        val cursor = dbHelper.getTasksByUser(username)
        while (cursor.moveToNext()) {
            tasks.add(
                Task(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    username = cursor.getString(cursor.getColumnIndexOrThrow("username")),
                    title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    dueDate = cursor.getString(cursor.getColumnIndexOrThrow("due_date")),
                    dueTime = cursor.getString(cursor.getColumnIndexOrThrow("due_time")),
                    fileUri = cursor.getString(cursor.getColumnIndexOrThrow("file_uri")),
                    status = cursor.getInt(cursor.getColumnIndexOrThrow("status")) == 1
                )
            )
        }
        cursor.close()
        return tasks
    }

    fun updateTaskStatus(taskId: Int, isDone: Boolean): Boolean {
        return dbHelper.updateTaskStatus(taskId, isDone)
    }

    fun deleteTask(taskId: Int): Boolean {
        return dbHelper.deleteTask(taskId)
    }

    // You can add more business logic methods as needed
}
