package com.example.todolist.presentation

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.database.MeetingRepositoryImpl
import com.example.todolist.domain.MeetingData
import com.example.todolist.domain.MeetingManager
import com.example.todolist.domain.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MeetingsActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var rvMeetings: RecyclerView
    private lateinit var meetingManager: MeetingManager
    private lateinit var username: String
    private var selectedDate: String = ""
    private lateinit var adapter: MeetingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meetings)

        username = intent.getStringExtra("username") ?: ""
        calendarView = findViewById(R.id.calendarView)
        rvMeetings = findViewById(R.id.rvMeetings)
        val btnAddMeeting = findViewById<Button>(R.id.btnAddMeeting)

        // TODO: Consider injecting MeetingManager via DI (e.g., Hilt) for testability
        meetingManager = MeetingManager(MeetingRepositoryImpl())
        selectedDate = getDateString(calendarView.date)

        adapter = MeetingAdapter { meeting -> showMeetingOptionsDialog(meeting) }
        rvMeetings.layoutManager = LinearLayoutManager(this)
        rvMeetings.adapter = adapter

        calendarView.setOnDateChangeListener { _, year, month, day ->
            selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
            loadMeetings()
        }

        btnAddMeeting.setOnClickListener {
            showAddMeetingDialog()
        }

        loadMeetings()
    }

    private fun getDateString(millis: Long): String {
        val c = java.util.Calendar.getInstance()
        c.timeInMillis = millis
        return String.format(
            "%04d-%02d-%02d",
            c.get(java.util.Calendar.YEAR),
            c.get(java.util.Calendar.MONTH) + 1,
            c.get(java.util.Calendar.DAY_OF_MONTH)
        )
    }

    private fun loadMeetings() {
        CoroutineScope(Dispatchers.Main).launch {
            when (val result = meetingManager.getMeetingsByUser(username)) {
                is Result.Success -> {
                    val filteredMeetings = result.data.filter { it.date == selectedDate }
                    adapter.submitList(filteredMeetings)
                }
                is Result.Error -> {
                    Toast.makeText(
                        this@MeetingsActivity,
                        "Error loading meetings: ${result.exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showAddMeetingDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_meeting, null)
        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etPurpose = view.findViewById<EditText>(R.id.etPurpose)
        val etAttendees = view.findViewById<EditText>(R.id.etAttendees)
        val etTime = view.findViewById<EditText>(R.id.etTime)

        AlertDialog.Builder(this)
            .setTitle("Add Meeting")
            .setView(view)
            .setPositiveButton("Add") { _, _ ->
                val title = etTitle.text.toString()
                val time = etTime.text.toString()
                if (title.isBlank() || !time.matches(Regex("\\d{2}:\\d{2}"))) {
                    Toast.makeText(
                        this,
                        "Title and time (HH:mm) are required",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }
                val newMeeting = MeetingData(
                    title = title,
                    purpose = etPurpose.text.toString().ifBlank { null },
                    attendees = etAttendees.text.toString().ifBlank { null },
                    time = time,
                    date = selectedDate,
                    username = username
                )
                CoroutineScope(Dispatchers.Main).launch {
                    when (val result = meetingManager.insertMeeting(newMeeting)) {
                        is Result.Success -> loadMeetings()
                        is Result.Error -> {
                            Toast.makeText(
                                this@MeetingsActivity,
                                "Error adding meeting: ${result.exception.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showMeetingOptionsDialog(meeting: MeetingData) {
        val options = arrayOf("Delete") // Edit disabled until PUT endpoint is implemented
        AlertDialog.Builder(this)
            .setTitle(meeting.title)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> CoroutineScope(Dispatchers.Main).launch {
                        when (val result = meetingManager.deleteMeeting(meeting.id)) {
                            is Result.Success -> loadMeetings()
                            is Result.Error -> {
                                Toast.makeText(
                                    this@MeetingsActivity,
                                    "Error deleting meeting: ${result.exception.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
            .show()
    }

    /*
    // Disabled until PUT endpoint is implemented in ASP.NET API
    private fun showEditDialog(meeting: MeetingData) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_meeting, null)
        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etPurpose = view.findViewById<EditText>(R.id.etPurpose)
        val etAttendees = view.findViewById<EditText>(R.id.etAttendees)
        val etTime = view.findViewById<EditText>(R.id.etTime)

        etTitle.setText(meeting.title)
        etPurpose.setText(meeting.purpose ?: "")
        etAttendees.setText(meeting.attendees ?: "")
        etTime.setText(meeting.time)

        AlertDialog.Builder(this)
            .setTitle("Edit Meeting")
            .setView(view)
            .setPositiveButton("Update") { _, _ ->
                val title = etTitle.text.toString()
                val time = etTime.text.toString()
                if (title.isBlank() || !time.matches(Regex("\\d{2}:\\d{2}"))) {
                    Toast.makeText(
                        this,
                        "Title and time (HH:mm) are required",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }
                val updatedMeeting = meeting.copy(
                    title = title,
                    purpose = etPurpose.text.toString().ifBlank { null },
                    attendees = etAttendees.text.toString().ifBlank { null },
                    time = time
                )
                CoroutineScope(Dispatchers.Main).launch {
                    when (val result = meetingManager.updateMeeting(updatedMeeting)) {
                        is Result.Success -> loadMeetings()
                        is Result.Error -> {
                            Toast.makeText(
                                this@MeetingsActivity,
                                "Error updating meeting: ${result.exception.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    */
}