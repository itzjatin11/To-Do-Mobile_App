// domain/LectureRepository.kt
package com.example.todolist.domain

import com.example.todolist.database.RetrofitClient

class LectureRepository {
    private val api = RetrofitClient.apiLecture


    suspend fun getAllLectures(): List<LectureData>? {
        val response = api.getAllLectures()
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun insertLecture(lecture: LectureData): Boolean {
        val response = api.addLecture(lecture)
        return response.isSuccessful
    }

    suspend fun updateLecture(lecture: LectureData): Boolean {
        val response = api.updateLecture(lecture.id, lecture)
        return response.isSuccessful
    }

    suspend fun deleteLecture(id: Int): Boolean {
        val response = api.deleteLecture(id)
        return response.isSuccessful
    }
}
