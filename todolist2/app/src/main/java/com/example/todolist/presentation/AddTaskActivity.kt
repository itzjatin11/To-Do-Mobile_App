package com.example.todolist.presentation

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.R
import com.example.todolist.database.DBHelper
import com.example.todolist.domain.Task
import com.example.todolist.domain.TaskManager
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    private lateinit var db: DBHelper
    private lateinit var taskManager: TaskManager

    private lateinit var title: EditText
    private lateinit var description: EditText
    private lateinit var dueDateText: TextView
    private lateinit var dueTimeText: TextView
    private lateinit var save: Button
    private lateinit var reminderCheckbox: CheckBox
    private lateinit var reminderTimePicker: TimePicker

    private var attachedFileUri: Uri? = null
    private var selectedDate = ""
    private var selectedTime = ""
    private var reminderHour = -1
    private var reminderMinute = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        db = DBHelper(this)
        taskManager = TaskManager(db)

        title = findViewById(R.id.etTaskTitle)
        description = findViewById(R.id.etTaskDescription)
        dueDateText = findViewById(R.id.tvDueDate)
        dueTimeText = findViewById(R.id.tvDueTime)
        save = findViewById(R.id.btnSaveTask)
        reminderCheckbox = findViewById(R.id.cbReminder)
        reminderTimePicker = findViewById(R.id.tpReminderTime)

        reminderTimePicker.setIs24HourView(true)

        findViewById<Button>(R.id.btnAttachFile).setOnClickListener {
            filePickerLauncher.launch("*/*")
        }

        val username = intent.getStringExtra("username")

        dueDateText.setOnClickListener { pickDate() }
        dueTimeText.setOnClickListener { pickTime() }

        reminderCheckbox.setOnCheckedChangeListener { _, isChecked ->
            reminderTimePicker.visibility = if (isChecked) TimePicker.VISIBLE else TimePicker.GONE
        }

        // Ask for notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }

        save.setOnClickListener {
            val taskTitle = title.text.toString().trim()
            val taskDesc = description.text.toString().trim()

            if (username != null && taskTitle.isNotEmpty() && taskDesc.isNotEmpty()
                && selectedDate.isNotEmpty() && selectedTime.isNotEmpty()
            ) {
                if (reminderCheckbox.isChecked) {
                    reminderHour = reminderTimePicker.hour
                    reminderMinute = reminderTimePicker.minute
                }

                val task = Task(
                    id = 0,
                    username = username,
                    title = taskTitle,
                    description = taskDesc,
                    dueDate = selectedDate,
                    dueTime = selectedTime,
                    fileUri = attachedFileUri?.toString() ?: "",
                    status = false,
                    reminderHour = reminderHour,
                    reminderMinute = reminderMinute
                )

                if (taskManager.addTask(task)) {
                    Toast.makeText(this, "Task Added", Toast.LENGTH_SHORT).show()
                    if (reminderHour >= 0 && reminderMinute >= 0) {
                        scheduleReminder(taskTitle, taskDesc, reminderHour, reminderMinute)
                    }
                    resetFields()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to Add Task", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "All fields including date & time are required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetFields() {
        title.text.clear()
        description.text.clear()
        dueDateText.text = "Select Due Date"
        dueTimeText.text = "Select Due Time"
        selectedDate = ""
        selectedTime = ""
        reminderCheckbox.isChecked = false
        reminderTimePicker.visibility = TimePicker.GONE
    }

    private fun pickDate() {
        val c = Calendar.getInstance()
        DatePickerDialog(this, { _, y, m, d ->
            selectedDate = String.format("%04d-%02d-%02d", y, m + 1, d)
            dueDateText.text = selectedDate
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun pickTime() {
        val c = Calendar.getInstance()
        TimePickerDialog(this, { _, h, m ->
            selectedTime = String.format("%02d:%02d", h, m)
            dueTimeText.text = selectedTime
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
    }

    private fun scheduleReminder(title: String, desc: String, hour: Int, minute: Int) {
        if (!canScheduleExactAlarms()) {
            Toast.makeText(this, "Please allow exact alarms in settings", Toast.LENGTH_LONG).show()
            requestExactAlarmPermission()
            return
        }

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        val intent = Intent(this, ReminderReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("description", desc)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } catch (e: SecurityException) {
            Toast.makeText(this, "Exact alarm permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            startActivity(intent)
        }
    }

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            attachedFileUri = uri
            Toast.makeText(this, "File attached: ${uri.lastPathSegment}", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
        }
    }
}
