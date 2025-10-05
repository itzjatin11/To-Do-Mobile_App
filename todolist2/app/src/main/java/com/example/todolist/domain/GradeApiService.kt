package com.example.todolist.domain

import retrofit2.Call
import retrofit2.http.*

interface GradeApiService {

    @GET("api/grades/user/{username}")
    fun getGradesByUser(@Path("username") username: String): Call<List<Grade>>

    @POST("api/grades")
    fun addGrade(@Body grade: Grade): Call<Void>

    @PUT("api/grades/{id}")
    fun updateGrade(@Path("id") id: String, @Body grade: Grade): Call<Void>

    @DELETE("api/grades/{id}")
    fun deleteGrade(@Path("id") id: String): Call<Void>
}
