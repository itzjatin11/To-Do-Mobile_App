package com.example.todolist.database

import com.example.todolist.domain.MeetingData
import com.example.todolist.domain.MeetingRepository

class MeetingRepositoryImpl : MeetingRepository {

    private val api: MeetingApiService = RetrofitClient.apiMeeting

    override suspend fun getMeetingsByUser(username: String): List<MeetingData> {
        return api.getMeetingsByUser(username)
    }

    override suspend fun insertMeeting(meeting: MeetingData) {
        val response = api.insertMeeting(meeting)
        if (!response.isSuccessful) {
            throw Exception("Failed to insert meeting: ${response.message()}")
        }
    }

    override suspend fun deleteMeeting(id: Int) {
        val response = api.deleteMeeting(id)
        if (!response.isSuccessful) {
            throw Exception("Failed to delete meeting: ${response.message()}")
        }
    }

    // Commented out until PUT endpoint is implemented
    /*
    override suspend fun updateMeeting(meeting: MeetingData) {
        val response = api.updateMeeting(meeting.id, meeting)
        if (!response.isSuccessful) {
            throw Exception("Failed to update meeting: ${response.message()}")
        }
    }
    */
}