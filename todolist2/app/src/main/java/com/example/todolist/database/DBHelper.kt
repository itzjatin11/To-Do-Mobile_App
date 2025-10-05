package com.example.todolist.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.todolist.domain.LectureData
import com.example.todolist.domain.Task

class DBHelper(context: Context) : SQLiteOpenHelper(context, "Userdata.db", null, 4) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE users(username TEXT PRIMARY KEY, password TEXT)")
        db.execSQL("""
            CREATE TABLE tasks(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT,
                title TEXT,
                description TEXT,
                due_date TEXT,
                due_time TEXT,
                file_uri TEXT,
                status INTEGER DEFAULT 0
            )
        """.trimIndent())
        db.execSQL("CREATE TABLE lectures(id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, topic TEXT, date TEXT, time TEXT, notes TEXT)")
        // Removed grades table creation here
        db.execSQL("CREATE TABLE meetings(id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, title TEXT, date TEXT, time TEXT, purpose TEXT, attendees TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS tasks")
        db.execSQL("DROP TABLE IF EXISTS lectures")
        // Removed grades drop table here
        db.execSQL("DROP TABLE IF EXISTS meetings")
        onCreate(db)
    }

    // --- USER METHODS ---
    fun insertUser(username: String, password: String): Boolean {
        val values = ContentValues().apply {
            put("username", username)
            put("password", password)
        }
        return writableDatabase.insert("users", null, values) != -1L
    }

    fun checkUser(username: String, password: String): Boolean {
        val cursor = readableDatabase.rawQuery("SELECT * FROM users WHERE username=? AND password=?", arrayOf(username, password))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun getAllUsers(): Cursor = readableDatabase.rawQuery("SELECT * FROM users", null)

    fun updateUser(oldUsername: String, newUsername: String, newPassword: String): Boolean {
        val values = ContentValues().apply {
            put("username", newUsername)
            put("password", newPassword)
        }
        return writableDatabase.update("users", values, "username = ?", arrayOf(oldUsername)) > 0
    }

    fun deleteUser(username: String): Boolean =
        writableDatabase.delete("users", "username = ?", arrayOf(username)) > 0

    // --- TASK METHODS ---
    fun insertTask(task: Task): Boolean {
        val values = ContentValues().apply {
            put("username", task.username)
            put("title", task.title)
            put("description", task.description)
            put("due_date", task.dueDate)
            put("due_time", task.dueTime)
            put("file_uri", task.fileUri)
        }
        return writableDatabase.insert("tasks", null, values) != -1L
    }

    fun getTaskById(id: Int): Cursor =
        readableDatabase.rawQuery("SELECT * FROM tasks WHERE id=?", arrayOf(id.toString()))

    fun getTasksByUser(username: String): Cursor =
        readableDatabase.rawQuery("SELECT * FROM tasks WHERE username=?", arrayOf(username))

    fun updateTask(id: Int, title: String, description: String, dueDate: String, dueTime: String, fileUri: String?): Boolean {
        val values = ContentValues().apply {
            put("title", title)
            put("description", description)
            put("due_date", dueDate)
            put("due_time", dueTime)
            put("file_uri", fileUri)
        }
        return writableDatabase.update("tasks", values, "id=?", arrayOf(id.toString())) > 0
    }

    fun updateTaskStatus(taskId: Int, isDone: Boolean): Boolean {
        val values = ContentValues().apply {
            put("status", if (isDone) 1 else 0)
        }
        return writableDatabase.update("tasks", values, "id=?", arrayOf(taskId.toString())) > 0
    }

    fun deleteTask(id: Int): Boolean =
        writableDatabase.delete("tasks", "id=?", arrayOf(id.toString())) > 0

    // --- LECTURE METHODS ---
    fun insertLecture(topic: String, date: String, time: String, notes: String, username: String): Boolean {
        val values = ContentValues().apply {
            put("username", username)
            put("topic", topic)
            put("date", date)
            put("time", time)
            put("notes", notes)
        }
        return writableDatabase.insert("lectures", null, values) != -1L
    }

    fun getLectureByDate(date: String, username: String): LectureData? {
        val cursor = readableDatabase.rawQuery("SELECT * FROM lectures WHERE date = ? AND username = ?", arrayOf(date, username))
        return if (cursor.moveToFirst()) {
            val data = LectureData(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                topic = cursor.getString(cursor.getColumnIndexOrThrow("topic")),
                date = date,
                time = cursor.getString(cursor.getColumnIndexOrThrow("time")),
                notes = cursor.getString(cursor.getColumnIndexOrThrow("notes"))
            )
            cursor.close()
            data
        } else {
            cursor.close()
            null
        }
    }

    fun updateLecture(id: Int, topic: String, date: String, time: String, notes: String, username: String): Boolean {
        val values = ContentValues().apply {
            put("topic", topic)
            put("date", date)
            put("time", time)
            put("notes", notes)
        }
        return writableDatabase.update("lectures", values, "id=? AND username=?", arrayOf(id.toString(), username)) > 0
    }

    fun getAllLecturesByUser(username: String): Cursor =
        readableDatabase.rawQuery("SELECT * FROM lectures WHERE username=?", arrayOf(username))

    fun deleteLecture(id: Int): Boolean =
        writableDatabase.delete("lectures", "id=?", arrayOf(id.toString())) > 0

    // --- MEETING METHODS ---
    fun insertMeeting(title: String, date: String, time: String, purpose: String, attendees: String, username: String): Boolean {
        val values = ContentValues().apply {
            put("username", username)
            put("title", title)
            put("date", date)
            put("time", time)
            put("purpose", purpose)
            put("attendees", attendees)
        }
        return writableDatabase.insert("meetings", null, values) != -1L
    }

    fun getMeetingsByUser(username: String): Cursor =
        readableDatabase.rawQuery("SELECT * FROM meetings WHERE username=?", arrayOf(username))

    fun updateMeeting(id: Int, title: String, date: String, time: String, purpose: String, username: String): Boolean {
        val values = ContentValues().apply {
            put("title", title)
            put("date", date)
            put("time", time)
            put("purpose", purpose)
        }
        return writableDatabase.update("meetings", values, "id=? AND username=?", arrayOf(id.toString(), username)) > 0
    }

    fun deleteMeeting(id: Int): Boolean =
        writableDatabase.delete("meetings", "id=?", arrayOf(id.toString())) > 0
}
