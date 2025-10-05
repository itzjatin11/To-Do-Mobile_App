package com.example.todolist.domain

class LectureManager(private val repo: LectureRepository) {

    suspend fun getAllLectures(): List<LectureData> = repo.getAllLectures() ?: emptyList()

    suspend fun insertLecture(lecture: LectureData): Boolean = repo.insertLecture(lecture)

    suspend fun updateLecture(lecture: LectureData): Boolean = repo.updateLecture(lecture)

    suspend fun deleteLecture(id: Int): Boolean = repo.deleteLecture(id)
}
