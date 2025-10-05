package com.example.todolist.database

import com.example.todolist.domain.GradeApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
//    private const val BASE_URL = "http://192.168.1.239:5000/" // ✅ Your correct local IP
//    private const val BASE_URL = "http://192.168.1.5:5000/"

    private const val BASE_URL = "http://192.168.1.239:5000/"





    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val gradeApiService: GradeApiService = retrofit.create(GradeApiService::class.java)
}

