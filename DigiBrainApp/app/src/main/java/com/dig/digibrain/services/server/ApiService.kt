package com.dig.digibrain.services.server

import com.dig.digibrain.models.*
import com.dig.digibrain.models.learnPaths.*
import com.dig.digibrain.models.postModels.quiz.AnswerPostModel
import com.dig.digibrain.models.postModels.quiz.QuestionPostModel
import com.dig.digibrain.models.postModels.quiz.QuizReportPostModel
import com.dig.digibrain.models.postModels.subject.ChapterPostModel
import com.dig.digibrain.models.postModels.subject.LessonPostModel
import com.dig.digibrain.models.postModels.subject.SubjectPostModel
import com.dig.digibrain.models.quiz.*
import com.dig.digibrain.models.subject.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // Authentication
    @POST("api/Auth/login")
    suspend fun login(@Body model: LoginModel): TokenModel
    @POST("api/Auth/register")
    suspend fun register(@Body model: RegisterModel): UserModel
    @GET("api/Auth/object-storage-info")
    suspend fun getObjectStorageInfo(@Header("Authorization") authHeader : String): ObjectStorageInfoModel

    // User
    // --GET--
    @GET("api/users/{username}")
    suspend fun getUserDetailsByUsername(@Path("username") username: String): UserModel
    // --PUT--
    @PUT("api/users/{username}/classes/{classId}")
    suspend fun updateClass(
        @Header("Authorization") authHeader : String,
        @Path("username") username: String,
        @Path("classId") classId: Long): UserModel
    @Multipart
    @PUT("api/users/{username}/profile-image")
    suspend fun changeProfilePicture(
        @Path("username") username: String,
        @Part file: MultipartBody.Part): UserModel
    // --DELETE--
    @DELETE("api/users/{username}")
    suspend fun deleteAccount(@Header("Authorization") authHeader : String, @Path("username") username: String): UserModel

    // Language
    @GET("api/languages")
    suspend fun getLanguages(): List<LanguageModel>

    // Theory
    //--GET--
    @GET("api/classes/all")
    suspend fun getAllClasses(): List<ClassModel>
    @GET("api/domains")
    suspend fun getAllDomains(): List<DomainModel>
    @GET("api/classes/domains")
    suspend fun getDomainsForClass(
        @Query("Number") number: Int,
        @Query("AtUniversity") atUniversity: Boolean,
        @Query("LanguageId") languageId: Long
    ): List<DomainModel>
    @GET("api/domains/{id}")
    suspend fun getDomainById(@Path("id") id: Long): DomainModel
    @GET("api/classes")
    suspend fun getClassByNumberAndDomain(
        @Query("Number") number: Int,
        @Query("AtUniversity") atUniversity: Boolean,
        @Query("DomainId") DomainId: Long
    ): ClassModel
    @GET("/api/classes/{id}")
    suspend fun getClassById(@Path("id") id: Long): ClassModel
    @GET("api/subjects/class/{classId}/languages/{languageId}")
    suspend fun getSubjectsForClass(@Path("classId") classId: Long, @Path("languageId") languageId: Long): List<SubjectModel>
    @GET("api/chapters/subject/{subjectId}")
    suspend fun getChaptersForSubject(@Path("subjectId") subjectId: Long): List<ChapterModel>
    @GET("api/lessons/chapter/{chapterId}")
    suspend fun getLessonsForChapter(@Path("chapterId") chapterId: Long): List<LessonModel>
    //--POST--
    @POST("api/chapters")
    suspend fun addChapter(@Header("Authorization") authHeader : String, @Body model: ChapterPostModel): ChapterModel
    @POST("api/lessons")
    suspend fun addLesson(@Header("Authorization") authHeader : String, @Body model: LessonPostModel): LessonModel
    @POST("api/subjects")
    suspend fun addSubject(@Header("Authorization") authHeader : String, @Body model: SubjectPostModel): SubjectModel
    @POST("api/subjects/ids")
    suspend fun getSubjectsForIds(@Body subjectIds: List<Long>): List<SubjectModel>

    // Quiz
    // --GET--
    @GET("api/questions")
    suspend fun getRandomQuestions(
        @Query("Number") number: Int,
        @Query("Difficulty") difficulty: String,
        @Query("Type") type: String,
        @Query("LanguageId") languageId: Long
    ): List<QuestionModel>
    @GET("api/questions/subject/{id}")
    suspend fun getRandomQuestionsForSubject(
        @Path("id") subjectId: Long,
        @Query("Number") number: Int,
        @Query("Difficulty") difficulty: String,
        @Query("Type") type: String,
        @Query("LanguageId") languageId: Long
    ): List<QuestionModel>
    @GET("api/answers/questions/{questionId}")
    suspend fun getQuestionAnswers(@Path("questionId") questionId: Long): List<AnswerModel>
    @GET("api/reports/users/{username}")
    suspend fun getUserReports(@Path("username") username: String): List<QuizReportModel>
    // --POST--
    @POST("api/questions/ids")
    suspend fun getQuestionsForIds(@Body questionIds: List<Long>): List<QuestionModel>
    @POST("api/quizes/users/{username}")
    suspend fun createQuizForUser(@Path("username") username: String): QuizModel
    @POST("api/quizes")
    suspend fun addQuestionToQuiz(@Body model: QuestionAnswerModel): QuizQuestionModel
    @POST("api/questions")
    suspend fun createQuestion(@Header("Authorization") authHeader : String, @Body model: QuestionPostModel): QuestionModel
    @POST("api/answers/multiple")
    suspend fun addMultipleAnswers(@Header("Authorization") authHeader : String, @Body answers: List<AnswerPostModel>): List<AnswerModel>
    @POST("api/questions/{id}/subjects")
    suspend fun addQuestionToSubjects(@Header("Authorization") authHeader : String, @Path("id") questionId: Long, @Body subjectIds: List<Long>): List<SubjectQuestionModel>
    @POST("api/reports")
    suspend fun addUserReport(@Body model: QuizReportPostModel): QuizReportModel

    // Learn Path
    // --GET--
    @GET("api/learn-paths")
    suspend fun getLearnPaths(): List<LearnPathModel>
    @GET("api/learn-paths/{id}")
    suspend fun getLearnPathDetails(@Path("id") id: Long): LearnPathExpandedModel
    @GET("api/learn-paths/users/{username}/status")
    suspend fun getLearnPathsStatusForUsername(@Path("username") username: String): List<LearnPathStatusModel>
    // --POST--
    @POST("api/learn-paths/{id}/users/{username}/status")
    suspend fun startLearnPath(
        @Header("Authorization") authHeader : String,
        @Path("id") id: Long,
        @Path("username") username: String): LearnPathStatusModel
    // --PUT--
    @PUT("api/learn-paths/{id}/users/{username}/status")
    suspend fun updateLearnPathStatus(
        @Header("Authorization") authHeader : String,
        @Path("id") id: Long,
        @Path("username") username: String,
        @Body model: LearnPathStatusUpdateModel): LearnPathStatusModel
    @PUT("api/learn-paths/{id}/users/{username}/status/quiz")
    suspend fun updateLearnPathQuizStatus(
        @Header("Authorization") authHeader : String,
        @Path("id") id: Long,
        @Path("username") username: String,
        @Body model: LearnPathQuizStatusUpdateModel): LearnPathStatusModel
}