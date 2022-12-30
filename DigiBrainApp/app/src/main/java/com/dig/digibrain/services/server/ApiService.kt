package com.dig.digibrain.services.server

import com.dig.digibrain.models.LoginModel
import com.dig.digibrain.models.RegisterModel
import com.dig.digibrain.models.TokenModel
import com.dig.digibrain.models.UserModel
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
}