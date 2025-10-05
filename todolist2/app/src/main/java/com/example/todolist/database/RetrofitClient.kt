package com.example.todolist.database

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Use this for physical devices; for emulators, use "http://10.0.2.2:5000/api/"
//    private const val BASE_URL = "http://192.168.1.239:5000/api/"
    // Alternatively, for a different device IP, you can use:
//    private const val BASE_URL = "http://172.20.10.3:5000/api/"
    private const val BASE_URL = "http://192.168.1.239:5000/api/"



    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiLecture: LectureApiService by lazy {
        retrofit.create(LectureApiService::class.java)
    }

    val apiMeeting: MeetingApiService by lazy {
        retrofit.create(MeetingApiService::class.java)
    }
}