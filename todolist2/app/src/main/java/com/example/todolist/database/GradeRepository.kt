package com.example.todolist.repository

import com.example.todolist.domain.Grade
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class GradeRepository {

    private val db = FirebaseFirestore.getInstance()
    private val gradesCollection = db.collection("grades")

    // Add new grade
    fun addGrade(grade: Grade, onComplete: (Boolean) -> Unit) {
        val data = hashMapOf(
            "username" to grade.username,
            "course" to grade.course,
            "type" to grade.type,
            "status" to grade.status,
            "due_date" to grade.dueDate
        )
        gradesCollection.add(data)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // Get real-time updates for user's grades
    fun listenGradesByUser(username: String, onChange: (List<Grade>) -> Unit): ListenerRegistration {
        return gradesCollection
            .whereEqualTo("username", username)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    onChange(emptyList())
                    return@addSnapshotListener
                }
                val grades = snapshot.documents.map { doc ->
                    Grade(
                        id = doc.id,
                        username = doc.getString("username") ?: "",
                        course = doc.getString("course") ?: "",
                        type = doc.getString("type") ?: "",
                        status = doc.getString("status") ?: "",
                        dueDate = doc.getString("due_date") ?: ""
                    )
                }
                onChange(grades)
            }
    }

    // Update grade by document id
    fun updateGrade(grade: Grade, onComplete: (Boolean) -> Unit) {
        val data = mapOf(
            "username" to grade.username,
            "course" to grade.course,
            "type" to grade.type,
            "status" to grade.status,
            "due_date" to grade.dueDate
        )
        gradesCollection.document(grade.id)
            .set(data)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // Delete grade by document id
    fun deleteGrade(gradeId: String, onComplete: (Boolean) -> Unit) {
        gradesCollection.document(gradeId)
            .delete()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}
