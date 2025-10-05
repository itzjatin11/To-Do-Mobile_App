package com.example.todolist.domain
//
//import com.example.todolist.repository.GradeRepository
//
//class GradeManager(private val repository: GradeRepository) {
//
//    fun insertGrade(username: String, course: String, type: String, status: String, dueDate: String, onComplete: (Boolean) -> Unit) {
//        val grade = Grade(
//            username = username,
//            course = course,
//            type = type,
//            status = status,
//            dueDate = dueDate
//        )
//        repository.addGrade(grade, onComplete)
//    }
//
//    fun getGradesByUser(username: String, onResult: (List<Grade>) -> Unit) {
//        repository.listenGradesByUser(username) { grades ->
//            onResult(grades)
//        }
//    }
//
//    fun deleteGrade(id: String, onComplete: (Boolean) -> Unit) {
//        repository.deleteGrade(id, onComplete)
//    }
//
//    fun updateGrade(grade: Grade, onComplete: (Boolean) -> Unit) {
//        repository.updateGrade(grade, onComplete)
//    }
//}
