package com.dig.digibrain.services.server

import com.dig.digibrain.models.LoginModel
import com.dig.digibrain.models.ObjectStorageInfoModel
import com.dig.digibrain.models.RegisterModel
import com.dig.digibrain.models.learnPaths.LearnPathQuizStatusUpdateModel
import com.dig.digibrain.models.learnPaths.LearnPathStatusUpdateModel
import com.dig.digibrain.models.postModels.quiz.AnswerPostModel
import com.dig.digibrain.models.postModels.quiz.QuestionPostModel
import com.dig.digibrain.models.postModels.quiz.QuizReportPostModel
import com.dig.digibrain.models.postModels.subject.ChapterPostModel
import com.dig.digibrain.models.postModels.subject.LessonPostModel
import com.dig.digibrain.models.postModels.subject.SubjectPostModel
import com.dig.digibrain.models.quiz.QuestionAnswerModel
import okhttp3.MultipartBody
import retrofit2.http.Header

class Repository(private val apiService: ApiService) {
    // Authentication
    suspend fun login(loginModel: LoginModel) = apiService.login(loginModel)
    suspend fun register(registerModel: RegisterModel) = apiService.register(registerModel)
    suspend fun getObjectStorageInfo(authHeader : String) = apiService.getObjectStorageInfo(authHeader)

    // User
    // --GET--
    suspend fun getUserDetailsByUsername(username: String) = apiService.getUserDetailsByUsername(username)
    // --PUT--
    suspend fun updateClass(authHeader: String, username: String, id: Long) = apiService.updateClass(authHeader, username, id)
    suspend fun changeProfilePicture(username: String, file: MultipartBody.Part) = apiService.changeProfilePicture(username, file)
    suspend fun requestRole(authHeader: String, username: String, role: String) = apiService.requestRole(authHeader, username, role)
    // --DELETE--
    suspend fun deleteAccount(authHeader: String, username: String) = apiService.deleteAccount(authHeader, username)

    // Language
    suspend fun getLanguages() = apiService.getLanguages()

    // Theory
    // --GET--
    suspend fun getAllClasses() = apiService.getAllClasses()
    suspend fun getAllDomains() = apiService.getAllDomains()
    suspend fun getDomainsForClass(number: Int, atUniversity: Boolean, languageId: Long) = apiService.getDomainsForClass(number, atUniversity, languageId)
    suspend fun getDomainById(id: Long) = apiService.getDomainById(id)
    suspend fun getClassByNumberAndDomain(number: Int, atUniversity: Boolean, domainId: Long) = apiService.getClassByNumberAndDomain(number, atUniversity, domainId)
    suspend fun getClassById(id: Long) = apiService.getClassById(id)
    suspend fun getSubjectsForClass(classId: Long, languageId: Long) = apiService.getSubjectsForClass(classId, languageId)
    suspend fun getChaptersForSubject(subjectId: Long) = apiService.getChaptersForSubject(subjectId)
    suspend fun getLessonsForChapter(chapterId: Long) = apiService.getLessonsForChapter(chapterId)
    // --POST --
    suspend fun addChapter(authHeader: String, model: ChapterPostModel) = apiService.addChapter(authHeader, model)
    suspend fun addLesson(authHeader: String, model: LessonPostModel) = apiService.addLesson(authHeader, model)
    suspend fun addSubject(authHeader: String, model: SubjectPostModel) = apiService.addSubject(authHeader, model)
    suspend fun getSubjectsForIds(subjectIds: List<Long>) = apiService.getSubjectsForIds(subjectIds)

    // Quiz
    // --GET--
    suspend fun getRandomQuestions(number: Int, difficulty: String, type: String, languageId: Long) = apiService.getRandomQuestions(number, difficulty, type, languageId)
    suspend fun getRandomQuestionsForSubject(subjectId: Long, number: Int, difficulty: String, type: String, languageId: Long) = apiService.getRandomQuestionsForSubject(subjectId, number, difficulty, type, languageId)
    suspend fun getQuestionAnswers(questionId: Long) = apiService.getQuestionAnswers(questionId)
    suspend fun getUserReports(username: String) = apiService.getUserReports(username)
    // --POST--
    suspend fun getQuestionsForIds(questionIds: List<Long>) = apiService.getQuestionsForIds(questionIds)
    suspend fun createQuizForUser(username: String) = apiService.createQuizForUser(username)
    suspend fun addQuestionToQuiz(model: QuestionAnswerModel) = apiService.addQuestionToQuiz(model)
    suspend fun createQuestion(authHeader: String, model: QuestionPostModel) = apiService.createQuestion(authHeader, model)
    suspend fun addMultipleAnswers(authHeader: String, answers: List<AnswerPostModel>) = apiService.addMultipleAnswers(authHeader, answers)
    suspend fun addQuestionToSubjects(authHeader: String, questionId: Long, subjectIds: List<Long>) = apiService.addQuestionToSubjects(authHeader, questionId, subjectIds)
    suspend fun addUserReport(model: QuizReportPostModel) = apiService.addUserReport(model)

    // Learn Path
    // --GET--
    suspend fun getLearnPaths() = apiService.getLearnPaths()
    suspend fun getLearnPathDetails(id: Long) = apiService.getLearnPathDetails(id)
    suspend fun getLearnPathsStatusForUsername(username: String) = apiService.getLearnPathsStatusForUsername(username)
    // --POST--
    suspend fun startLearnPath(authHeader: String, id: Long, username: String) = apiService.startLearnPath(authHeader, id, username)
    // --PUT--
    suspend fun updateLearnPathStatus(authHeader: String, id: Long, username: String, model: LearnPathStatusUpdateModel) = apiService.updateLearnPathStatus(authHeader, id, username, model)
    suspend fun updateLearnPathQuizStatus(authHeader: String, id: Long, username: String, model: LearnPathQuizStatusUpdateModel) = apiService.updateLearnPathQuizStatus(authHeader, id, username, model)
}