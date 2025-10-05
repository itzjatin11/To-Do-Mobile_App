package com.example.todolist.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.domain.Grade

class GradeAdapter(
    private val grades: List<Grade>,
    private val onEdit: (Grade) -> Unit,
    private val onDelete: (Grade) -> Unit
) : RecyclerView.Adapter<GradeAdapter.GradeViewHolder>() {

    class GradeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCourse: TextView = view.findViewById(R.id.tvCourse)
        val tvType: TextView = view.findViewById(R.id.tvType)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvDueDate: TextView = view.findViewById(R.id.tvDueDate)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GradeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grade, parent, false)
        return GradeViewHolder(view)
    }

    override fun onBindViewHolder(holder: GradeViewHolder, position: Int) {
        val grade = grades[position]
        holder.tvCourse.text = grade.course
        holder.tvType.text = grade.type
        holder.tvStatus.text = grade.status
        holder.tvDueDate.text = grade.dueDate

        holder.btnEdit.setOnClickListener {
            onEdit(grade)
        }

        holder.btnDelete.setOnClickListener {
            onDelete(grade)
        }
    }

    override fun getItemCount(): Int = grades.size
}
