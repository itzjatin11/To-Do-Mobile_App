package com.example.todolist.presentation

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.todolist.R
import com.example.todolist.domain.LectureData
import com.example.todolist.domain.LectureManager
import com.example.todolist.domain.LectureRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class LecturesActivity : AppCompatActivity() {

    private lateinit var lectureListView: ListView
    private lateinit var btnShowCalendar: Button
    private lateinit var btnSearch: Button
    private lateinit var etSearch: EditText
    private lateinit var calendarView: CalendarView
    private lateinit var lectureAdapter: ArrayAdapter<LectureData>

    private val manager = LectureManager(LectureRepository())
    private var selectedDate: String = ""
    private var username: String? = null
    private val lectureList = mutableListOf<LectureData>()
    private val filteredLectureList = mutableListOf<LectureData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lectures2)

        username = intent.getStringExtra("username")

        calendarView = findViewById(R.id.calendarView)
        btnShowCalendar = findViewById(R.id.btnShowCalendar)
        btnSearch = findViewById(R.id.btnSearch)
        etSearch = findViewById(R.id.etSearch)
        lectureListView = findViewById(R.id.lvLectureList)
        calendarView.visibility = View.GONE
        etSearch.visibility = View.GONE

        // Setup Search button
        btnSearch.setOnClickListener {
            etSearch.visibility = if (etSearch.visibility == View.GONE) View.VISIBLE else View.GONE
            if (etSearch.visibility == View.GONE) {
                etSearch.text.clear()
                filteredLectureList.clear()
                filteredLectureList.addAll(lectureList)
                lectureAdapter.notifyDataSetChanged()
            }
        }

        // Setup Search EditText
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterLectures(s.toString())
            }
        })

        btnShowCalendar.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                selectedDate = "%02d/%02d/%04d".format(day, month + 1, year)
                showLectureDialog(null)
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }

        selectedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        loadLectures()

        calendarView.setOnDateChangeListener { _, year, month, day ->
            selectedDate = "%02d/%02d/%04d".format(day, month + 1, year)
            showLectureDialog(null)
        }
    }

    private fun loadLectures() {
        lifecycleScope.launch {
            try {
                val allLectures = manager.getAllLectures()
                lectureList.clear()
                filteredLectureList.clear()
                lectureList.addAll(allLectures.filter { it.notes.contains(username ?: "") })
                filteredLectureList.addAll(lectureList)
                setupListAdapter()
            } catch (e: Exception) {
                Toast.makeText(this@LecturesActivity, "Error loading lectures", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListAdapter() {
        lectureAdapter = object : ArrayAdapter<LectureData>(
            this@LecturesActivity,
            R.layout.item_lecture,
            filteredLectureList
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_lecture, parent, false)

                val tvTopic = view.findViewById<TextView>(R.id.tvTopic)
                val tvDate = view.findViewById<TextView>(R.id.tvDate)
                val tvTime = view.findViewById<TextView>(R.id.tvTime)
                val tvNotes = view.findViewById<TextView>(R.id.tvNotes)
                val btnEdit = view.findViewById<Button>(R.id.btnEdit)
                val btnDelete = view.findViewById<Button>(R.id.btnDelete)

                val lecture = getItem(position)!!

                tvTopic.text = lecture.topic
                tvDate.text = "Date: ${lecture.date}"
                tvTime.text = "Time: ${lecture.time}"
                tvNotes.text = lecture.notes

                btnEdit.setOnClickListener {
                    showLectureDialog(lecture)
                }

                btnDelete.setOnClickListener {
                    AlertDialog.Builder(this@LecturesActivity)
                        .setMessage("Delete ${lecture.topic}?")
                        .setPositiveButton("Yes") { _, _ ->
                            lifecycleScope.launch {
                                val success = manager.deleteLecture(lecture.id)
                                if (success) {
                                    Toast.makeText(this@LecturesActivity, "Lecture deleted", Toast.LENGTH_SHORT).show()
                                    loadLectures()
                                } else {
                                    Toast.makeText(this@LecturesActivity, "Failed to delete lecture", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        .setNegativeButton("No", null)
                        .show()
                }

                return view
            }
        }
        lectureListView.adapter = lectureAdapter
    }

    private fun filterLectures(query: String) {
        filteredLectureList.clear()
        if (query.isEmpty()) {
            filteredLectureList.addAll(lectureList)
        } else {
            filteredLectureList.addAll(lectureList.filter {
                it.topic.contains(query, ignoreCase = true) ||
                        it.notes.contains(query, ignoreCase = true) ||
                        it.date.contains(query, ignoreCase = true) ||
                        it.time.contains(query, ignoreCase = true)
            })
        }
        lectureAdapter.notifyDataSetChanged()
    }

    private fun showLectureDialog(lecture: LectureData?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_lecture, null)
        val etCourse = dialogView.findViewById<EditText>(R.id.etCourse)
        val etClassroom = dialogView.findViewById<EditText>(R.id.etClassroom)
        val etTime = dialogView.findViewById<EditText>(R.id.etTime)

        val isEditing = lecture != null

        if (isEditing) {
            etCourse.setText(lecture!!.topic)
            val parts = lecture.notes.split(" — ")
            etClassroom.setText(parts.getOrNull(0)?.replace("Classroom: ", "") ?: "")
            etTime.setText(parts.getOrNull(1) ?: "")
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(if (isEditing) "Edit Lecture on ${lecture?.date ?: selectedDate}" else "Add Lecture on $selectedDate")
            .setView(dialogView)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val topic = etCourse.text.toString().trim()
                val classroom = etClassroom.text.toString().trim()
                val time = etTime.text.toString().trim()
                val notes = "Classroom: $classroom — $time — ${username ?: ""}"

                if (topic.isEmpty() || classroom.isEmpty() || time.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val newLecture = LectureData(
                    id = lecture?.id ?: 0,
                    topic = topic,
                    date = lecture?.date ?: selectedDate,
                    time = time,
                    notes = notes
                )

                lifecycleScope.launch {
                    val success = if (isEditing) {
                        manager.updateLecture(newLecture)
                    } else {
                        manager.insertLecture(newLecture)
                    }

                    if (success) {
                        Toast.makeText(this@LecturesActivity, "Lecture saved", Toast.LENGTH_SHORT).show()
                        loadLectures()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(this@LecturesActivity, "Failed to save lecture", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        dialog.show()
    }
}