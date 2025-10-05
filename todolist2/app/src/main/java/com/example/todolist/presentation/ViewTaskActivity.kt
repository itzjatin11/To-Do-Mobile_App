// ViewTaskActivity.kt
package com.example.todolist.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.R
import com.example.todolist.domain.TaskManager
import com.example.todolist.database.DBHelper

class ViewTaskActivity : AppCompatActivity() {
    private lateinit var taskManager: TaskManager
    private lateinit var taskContainer: LinearLayout
    private var username: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_task)

        val dbHelper = DBHelper(this)
        taskManager = TaskManager(dbHelper)

        taskContainer = findViewById(R.id.taskContainer)
        username = intent.getStringExtra("username")

        loadTasks()
    }

    private fun loadTasks() {
        taskContainer.removeAllViews()
        val tasks = taskManager.getTasksByUser(username ?: "")
        for (task in tasks) {
            val taskView = layoutInflater.inflate(R.layout.item_task, taskContainer, false)

            val checkBox = taskView.findViewById<CheckBox>(R.id.checkDone)
            val titleView = taskView.findViewById<TextView>(R.id.taskTitle)
            val descView = taskView.findViewById<TextView>(R.id.taskDescription)
            val dueView = taskView.findViewById<TextView>(R.id.taskDueDate)
            val editBtn = taskView.findViewById<Button>(R.id.btnEdit)
            val deleteBtn = taskView.findViewById<Button>(R.id.btnDelete)
            val fileAttachmentText = taskView.findViewById<TextView>(R.id.fileAttachmentText)
            val fileAttachmentBtn = taskView.findViewById<Button>(R.id.fileAttachmentButton)

            checkBox.isChecked = task.status
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                taskManager.updateTaskStatus(task.id, isChecked)
            }

            titleView.text = task.title
            descView.text = task.description
            dueView.text = if (task.dueDate.isNotEmpty() || task.dueTime.isNotEmpty())
                "Due: ${if (task.dueDate.isNotEmpty()) task.dueDate else ""} ${if (task.dueTime.isNotEmpty()) task.dueTime else ""}"
            else "No due date/time"

            if (!task.fileUri.isNullOrEmpty()) {
                fileAttachmentText.visibility = View.VISIBLE
                fileAttachmentBtn.visibility = View.VISIBLE

                fileAttachmentText.text = "Attached: ${Uri.parse(task.fileUri).lastPathSegment}"
                fileAttachmentBtn.setOnClickListener {
                    try {
                        val file = java.io.File(task.fileUri)
                        val uri: Uri = if (file.exists()) {
                            Log.d("File", "Exists: ${file.exists()} - Path: ${task.fileUri}")
                            androidx.core.content.FileProvider.getUriForFile(
                                this@ViewTaskActivity,
                                "${applicationContext.packageName}.fileprovider",
                                file
                            )
                        } else {
                            Uri.parse(task.fileUri)
                        }

                        val mimeType = contentResolver.getType(uri) ?: "*/*"
                        val openIntent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uri, mimeType)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        startActivity(Intent.createChooser(openIntent, "Open with"))
                    } catch (e: Exception) {
                        Toast.makeText(this@ViewTaskActivity, "Cannot open this file", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                }
            } else {
                fileAttachmentText.visibility = View.GONE
                fileAttachmentBtn.visibility = View.GONE
            }

            editBtn.setOnClickListener {
                val intent = Intent(this@ViewTaskActivity, EditTaskActivity::class.java).apply {
                    putExtra("id", task.id)
                    putExtra("username", username)
                    putExtra("isLecture", false)
                    putExtra("isGrade", false)
                }
                startActivity(intent)
            }

            deleteBtn.setOnClickListener {
                AlertDialog.Builder(this@ViewTaskActivity)
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton("Yes") { _, _ ->
                        if (taskManager.deleteTask(task.id)) {
                            Toast.makeText(this@ViewTaskActivity, "Task deleted", Toast.LENGTH_SHORT).show()
                            loadTasks()
                        } else {
                            Toast.makeText(this@ViewTaskActivity, "Failed to delete task", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("No", null)
                    .show()
            }

            taskContainer.addView(taskView)
        }
    }

    override fun onResume() {
        super.onResume()
        loadTasks()
    }
}