package com.example.todolist.presentation

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.database.ApiClient
import com.example.todolist.domain.Grade
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GradingActivity : AppCompatActivity() {

    private lateinit var etCourse: EditText
    private lateinit var etType: EditText
    private lateinit var etDueDate: EditText
    private lateinit var rbPending: RadioButton
    private lateinit var rbMarked: RadioButton
    private lateinit var rgStatus: RadioGroup
    private lateinit var btnSaveGrade: Button
    private lateinit var rvGrades: RecyclerView

    private lateinit var username: String
    private val grades = mutableListOf<Grade>()
    private lateinit var adapter: GradeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grading)

        username = intent.getStringExtra("username") ?: ""

        etCourse = findViewById(R.id.etCourse)
        etType = findViewById(R.id.etType)
        etDueDate = findViewById(R.id.etDueDate)
        rbPending = findViewById(R.id.rbPending)
        rbMarked = findViewById(R.id.rbMarked)
        rgStatus = findViewById(R.id.rgStatus)
        btnSaveGrade = findViewById(R.id.btnSaveGrade)
        rvGrades = findViewById(R.id.rvGrades)

        adapter = GradeAdapter(grades,
            onEdit = { grade -> showEditDialog(grade) },
            onDelete = { grade -> confirmDelete(grade) }
        )

        rvGrades.layoutManager = LinearLayoutManager(this)
        rvGrades.adapter = adapter

        btnSaveGrade.setOnClickListener {
            val newGrade = Grade(
                id = "", // Leave blank, backend will assign
                username = username,
                course = etCourse.text.toString(),
                type = etType.text.toString(),
                status = if (rbMarked.isChecked) "Marked" else "Pending",
                dueDate = etDueDate.text.toString()
            )

            ApiClient.gradeApiService.addGrade(newGrade)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@GradingActivity, "Saved", Toast.LENGTH_SHORT).show()
                            loadGrades()
                        } else {
                            Toast.makeText(this@GradingActivity, "Failed to save", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@GradingActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        loadGrades()
    }

    private fun loadGrades() {
        ApiClient.gradeApiService.getGradesByUser(username)
            .enqueue(object : Callback<List<Grade>> {
                override fun onResponse(call: Call<List<Grade>>, response: Response<List<Grade>>) {
                    if (response.isSuccessful) {
                        grades.clear()
                        response.body()?.let { grades.addAll(it) }
                        adapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<List<Grade>>, t: Throwable) {
                    Toast.makeText(this@GradingActivity, "Failed to load grades", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun confirmDelete(grade: Grade) {
        AlertDialog.Builder(this)
            .setTitle("Delete Entry")
            .setMessage("Are you sure you want to delete this entry?")
            .setPositiveButton("Yes") { _, _ ->
                ApiClient.gradeApiService.deleteGrade(grade.id)
                    .enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@GradingActivity, "Deleted", Toast.LENGTH_SHORT).show()
                                loadGrades()
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Toast.makeText(this@GradingActivity, "Error deleting", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditDialog(grade: Grade) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_grade, null)
        val etCourseEdit = dialogView.findViewById<EditText>(R.id.etCourseEdit)
        val etTypeEdit = dialogView.findViewById<EditText>(R.id.etTypeEdit)
        val etDueDateEdit = dialogView.findViewById<EditText>(R.id.etDueDateEdit)
        val rbPendingEdit = dialogView.findViewById<RadioButton>(R.id.rbPendingEdit)
        val rbMarkedEdit = dialogView.findViewById<RadioButton>(R.id.rbMarkedEdit)

        etCourseEdit.setText(grade.course)
        etTypeEdit.setText(grade.type)
        etDueDateEdit.setText(grade.dueDate)
        if (grade.status == "Marked") rbMarkedEdit.isChecked = true else rbPendingEdit.isChecked = true

        AlertDialog.Builder(this)
            .setTitle("Edit Grade")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val updatedGrade = Grade(
                    id = grade.id,
                    username = grade.username,
                    course = etCourseEdit.text.toString(),
                    type = etTypeEdit.text.toString(),
                    dueDate = etDueDateEdit.text.toString(),
                    status = if (rbMarkedEdit.isChecked) "Marked" else "Pending"
                )

                ApiClient.gradeApiService.updateGrade(updatedGrade.id, updatedGrade)
                    .enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@GradingActivity, "Updated", Toast.LENGTH_SHORT).show()
                                loadGrades()
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Toast.makeText(this@GradingActivity, "Error updating", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
