package com.example.todolist.domain

interface MeetingRepository {
    suspend fun getMeetingsByUser(username: String): List<MeetingData>
    suspend fun insertMeeting(meeting: MeetingData)
    suspend fun deleteMeeting(id: Int)
    // Commented out until PUT endpoint is implemented in ASP.NET API
    // suspend fun updateMeeting(meeting: MeetingData)
}