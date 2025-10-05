package com.example.todolist.presentation

import android.app.*
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.R
import com.example.todolist.database.DBHelper
import com.example.todolist.domain.Grade
import com.example.todolist.repository.GradeRepository
import java.util.*

class EditTaskActivity : AppCompatActivity() {

    private lateinit var db: DBHelper
    private val gradeRepo = GradeRepository()

    private lateinit var title: EditText
    private lateinit var description: EditText
    private lateinit var dueDateText: TextView
    private lateinit var dueTimeText: TextView
    private lateinit var save: Button
    private lateinit var delete: Button

    private var selectedDate = ""
    private var selectedTime = ""
    private var isGrade = false
    private var isLecture = false
    private var isMeeting = false

    private var recordId = -1
    private var username: String? = null
    private var attachedFileUri: Uri? = null
    private var firestoreGradeId: String? = null // For Firestore-based grades

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)

        db = DBHelper(this)

        title = findViewById(R.id.etTaskTitle)
        description = findViewById(R.id.etTaskDescription)
        dueDateText = findViewById(R.id.tvDueDate)
        dueTimeText = findViewById(R.id.tvDueTime)
        save = findViewById(R.id.btnSaveTask)

        delete = Button(this).apply {
            text = "Delete"
            setBackgroundColor(0xFFFF4444.toInt())
            setTextColor(0xFFFFFFFF.toInt())
        }
        (save.parent as LinearLayout).addView(delete)

        isGrade = intent.getBooleanExtra("isGrade", false)
        isLecture = intent.getBooleanExtra("isLecture", false)
        isMeeting = intent.getBooleanExtra("isMeeting", false)
        recordId = intent.getIntExtra("id", -1)
        username = intent.getStringExtra("username")
        firestoreGradeId = intent.getStringExtra("id") // If coming from Firestore

        if (recordId == -1 && firestoreGradeId == null) {
            Toast.makeText(this, "Invalid data", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        when {
            isMeeting -> loadMeetingDetails(recordId)
            isLecture -> loadLectureDetails(recordId)
            isGrade -> loadGradeDetailsFirestore(firestoreGradeId!!)
            else -> loadTaskDetails(recordId)
        }

        dueDateText.setOnClickListener { pickDate() }
        dueTimeText.setOnClickListener {
            if (!isGrade && !isLecture) pickTime()
        }

        save.setOnClickListener {
            val t = title.text.toString().trim()
            val d = description.text.toString().trim()

            if (t.isEmpty() || selectedDate.isEmpty() || (!isGrade && !isLecture && selectedTime.isEmpty())) {
                Toast.makeText(this, "All required fields must be filled", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            when {
                isGrade -> {
                    val updatedGrade = Grade(
                        id = firestoreGradeId!!,
                        username = username ?: "",
                        course = t,
                        type = d,
                        status = "Pending",
                        dueDate = selectedDate
                    )
                    gradeRepo.updateGrade(updatedGrade) { success ->
                        if (success) {
                            Toast.makeText(this, "Grade Updated", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                isLecture -> {
                    val success = db.updateLecture(recordId, t, selectedDate, selectedTime, d, username ?: "")
                    showResult(success, "Lecture")
                }
                isMeeting -> {
                    val success = db.updateMeeting(recordId, t, selectedDate, selectedTime, d, username ?: "")
                    showResult(success, "Meeting")
                }
                else -> {
                    val success = db.updateTask(recordId, t, d, selectedDate, selectedTime, attachedFileUri?.toString())
                    showResult(success, "Task")
                }
            }
        }

        delete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Confirmation")
                .setMessage("Are you sure you want to delete this ${getItemLabel()}?")
                .setPositiveButton("Yes") { _, _ ->
                    when {
                        isGrade -> {
                            gradeRepo.deleteGrade(firestoreGradeId!!) { success ->
                                if (success) {
                                    Toast.makeText(this, "Grade Deleted", Toast.LENGTH_SHORT).show()
                                    finish()
                                } else {
                                    Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        isLecture -> showResult(db.deleteLecture(recordId), "Lecture", true)
                        isMeeting -> showResult(db.deleteMeeting(recordId), "Meeting", true)
                        else -> showResult(db.deleteTask(recordId), "Task", true)
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    private fun showResult(success: Boolean, label: String, isDelete: Boolean = false) {
        Toast.makeText(
            this,
            if (success) "$label ${if (isDelete) "Deleted" else "Updated"}" else "${if (isDelete) "Delete" else "Update"} failed",
            Toast.LENGTH_SHORT
        ).show()
        if (success) finish()
    }

    private fun getItemLabel(): String {
        return when {
            isGrade -> "grade"
            isLecture -> "lecture"
            isMeeting -> "meeting"
            else -> "task"
        }
    }

    private fun loadMeetingDetails(id: Int) {
        val cursor = db.readableDatabase.rawQuery("SELECT * FROM meetings WHERE id=?", arrayOf(id.toString()))
        if (cursor.moveToFirst()) {
            title.setText(cursor.getString(cursor.getColumnIndexOrThrow("title")))
            description.setText(cursor.getString(cursor.getColumnIndexOrThrow("purpose")))
            selectedDate = cursor.getString(cursor.getColumnIndexOrThrow("date"))
            selectedTime = cursor.getString(cursor.getColumnIndexOrThrow("time"))
            dueDateText.text = selectedDate
            dueTimeText.text = selectedTime
        }
        cursor.close()
    }

    private fun loadLectureDetails(id: Int) {
        val cursor = db.readableDatabase.rawQuery("SELECT * FROM lectures WHERE id=?", arrayOf(id.toString()))
        if (cursor.moveToFirst()) {
            title.setText(cursor.getString(cursor.getColumnIndexOrThrow("topic")))
            description.setText(cursor.getString(cursor.getColumnIndexOrThrow("notes")))
            selectedDate = cursor.getString(cursor.getColumnIndexOrThrow("date"))
            selectedTime = cursor.getString(cursor.getColumnIndexOrThrow("time"))
            dueDateText.text = selectedDate
            dueTimeText.text = selectedTime
        }
        cursor.close()
    }

    private fun loadTaskDetails(id: Int) {
        val cursor = db.getTaskById(id)
        if (cursor.moveToFirst()) {
            title.setText(cursor.getString(cursor.getColumnIndexOrThrow("title")))
            description.setText(cursor.getString(cursor.getColumnIndexOrThrow("description")))
            selectedDate = cursor.getString(cursor.getColumnIndexOrThrow("due_date"))
            selectedTime = cursor.getString(cursor.getColumnIndexOrThrow("due_time"))
            dueDateText.text = selectedDate
            dueTimeText.text = selectedTime
        }
        cursor.close()
    }

    private fun loadGradeDetailsFirestore(gradeId: String) {
        // No need to read from local DB — already passed to activity via intent or real-time list
        // You can optionally query if needed, but for now just show placeholders or disable time
        dueTimeText.text = "N/A"
        dueTimeText.isEnabled = false
    }

    private fun pickDate() {
        val c = Calendar.getInstance()
        val dpd = DatePickerDialog(this, { _, y, m, d ->
            selectedDate = String.format("%04d-%02d-%02d", y, m + 1, d)
            dueDateText.text = selectedDate
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
        dpd.show()
    }

    private fun pickTime() {
        val c = Calendar.getInstance()
        val tpd = TimePickerDialog(this, { _, h, m ->
            selectedTime = String.format("%02d:%02d", h, m)
            dueTimeText.text = selectedTime
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true)
        tpd.show()
    }
}
