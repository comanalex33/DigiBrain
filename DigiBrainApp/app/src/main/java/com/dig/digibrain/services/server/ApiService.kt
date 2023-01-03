package com.dig.digibrain.services.server

import com.dig.digibrain.models.LoginModel
import com.dig.digibrain.models.RegisterModel
import com.dig.digibrain.models.TokenModel
import com.dig.digibrain.models.UserModel
import com.dig.digibrain.models.quiz.*
import com.dig.digibrain.models.subject.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Authentication
    @POST("api/Auth/login")
    suspend fun login(@Body model: LoginModel): TokenModel
    @POST("api/Auth/register")
    suspend fun register(@Body model: RegisterModel): UserModel

    // Theory
    @GET("api/classes/domains")
    suspend fun getDomainsForClass(
        @Query("Number") number: Int,
        @Query("AtUniversity") atUniversity: Boolean,
        @Query("LanguageId") languageId: Long
    ): List<DomainModel>
    @GET("api/classes")
    suspend fun getClassByNumberAndDomain(
        @Query("Number") number: Int,
        @Query("AtUniversity") atUniversity: Boolean,
        @Query("DomainId") DomainId: Long
    ): ClassModel
    @GET("api/subjects/class/{classId}")
    suspend fun getSubjectsForClass(@Path("classId") classId: Long): List<SubjectModel>
    @GET("api/chapters/subject/{subjectId}")
    suspend fun getChaptersForSubject(@Path("subjectId") subjectId: Long): List<ChapterModel>
    @GET("api/lessons/chapter/{chapterId}")
    suspend fun getLessonsForChapter(@Path("chapterId") chapterId: Long): List<LessonModel>

    // Quiz
    @GET("api/questions")
    suspend fun getRandomQuestions(
        @Query("Number") number: Int,
        @Query("Difficulty") difficulty: String,
        @Query("Type") type: String,
        @Query("LanguageId") languageId: Long
    ): List<QuestionModel>
    @GET("api/answers/questions/{questionId}")
    suspend fun getQuestionAnswers(@Path("questionId") questionId: Long): List<AnswerModel>
    @POST("api/quizes/users/{username}")
    suspend fun createQuizForUser(@Path("username") username: String): QuizModel
    @POST("api/quizes")
    suspend fun addQuestionToQuiz(@Body model: QuestionAnswerModel): QuizQuestionModel
}