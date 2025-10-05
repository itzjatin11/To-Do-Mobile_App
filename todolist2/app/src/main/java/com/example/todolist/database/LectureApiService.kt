package com.example.todolist.database

import com.example.todolist.domain.LectureData
import retrofit2.Response
import retrofit2.http.*

interface LectureApiService {

    @GET("lectures")
    suspend fun getAllLectures(): Response<List<LectureData>>

    @POST("lectures")
    suspend fun addLecture(@Body lecture: LectureData): Response<LectureData>

    @PUT("lectures/{id}")
    suspend fun updateLecture(
        @Path("id") id: Int,
        @Body lecture: LectureData
    ): Response<Void>

    @DELETE("lectures/{id}")
    suspend fun deleteLecture(
        @Path("id") id: Int
    ): Response<Void>
}
