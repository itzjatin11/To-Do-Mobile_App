package com.example.todolist.database

import com.example.todolist.domain.MeetingData
import retrofit2.Response
import retrofit2.http.*

interface MeetingApiService {

    @GET("Meetings/user/{username}")
    suspend fun getMeetingsByUser(@Path("username") username: String): List<MeetingData>

    @POST("Meetings")
    suspend fun insertMeeting(@Body meeting: MeetingData): Response<MeetingData>

    @DELETE("Meetings/{id}")
    suspend fun deleteMeeting(@Path("id") id: Int): Response<Unit>


}