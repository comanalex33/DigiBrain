package com.dig.digibrain.services.server

import com.dig.digibrain.models.LoginModel
import com.dig.digibrain.models.RegisterModel
import com.dig.digibrain.models.postModels.subject.ChapterPostModel
import com.dig.digibrain.models.postModels.subject.LessonPostModel
import com.dig.digibrain.models.postModels.subject.SubjectPostModel
import com.dig.digibrain.models.quiz.QuestionAnswerModel

class Repository(private val apiService: ApiService) {
    // Authentication
    suspend fun login(loginModel: LoginModel) = apiService.login(loginModel)
    suspend fun register(registerModel: RegisterModel) = apiService.register(registerModel)

    // Theory
    // --GET--
    suspend fun getDomainsForClass(number: Int, atUniversity: Boolean, languageId: Long) = apiService.getDomainsForClass(number, atUniversity, languageId)
    suspend fun getClassByNumberAndDomain(number: Int, atUniversity: Boolean, domainId: Long) = apiService.getClassByNumberAndDomain(number, atUniversity, domainId)
    suspend fun getSubjectsForClass(classId: Long) = apiService.getSubjectsForClass(classId)
    suspend fun getChaptersForSubject(subjectId: Long) = apiService.getChaptersForSubject(subjectId)
    suspend fun getLessonsForChapter(chapterId: Long) = apiService.getLessonsForChapter(chapterId)
    // --POST --
    suspend fun addChapter(authHeader: String, model: ChapterPostModel) = apiService.addChapter(authHeader, model)
    suspend fun addLesson(authHeader: String, model: LessonPostModel) = apiService.addLesson(authHeader, model)
    suspend fun addSubject(authHeader: String, model: SubjectPostModel) = apiService.addSubject(authHeader, model)

    // Quiz
    suspend fun getRandomQuestions(number: Int, difficulty: String, type: String, languageId: Long) = apiService.getRandomQuestions(number, difficulty, type, languageId)
    suspend fun getQuestionAnswers(questionId: Long) = apiService.getQuestionAnswers(questionId)
    suspend fun createQuizForUser(username: String) = apiService.createQuizForUser(username)
    suspend fun addQuestionToQuiz(model: QuestionAnswerModel) = apiService.addQuestionToQuiz(model)
}