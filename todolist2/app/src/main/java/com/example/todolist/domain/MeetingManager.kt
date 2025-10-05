package com.example.todolist.domain

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

class MeetingManager(private val repo: MeetingRepository) {

    suspend fun getMeetingsByUser(username: String): Result<List<MeetingData>> {
        return try {
            Result.Success(repo.getMeetingsByUser(username))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun insertMeeting(meeting: MeetingData): Result<Unit> {
        return try {
            repo.insertMeeting(meeting)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun deleteMeeting(id: Int): Result<Unit> {
        return try {
            repo.deleteMeeting(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }


}