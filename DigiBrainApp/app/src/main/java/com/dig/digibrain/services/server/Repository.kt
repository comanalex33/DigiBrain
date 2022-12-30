package com.dig.digibrain.services.server

import com.dig.digibrain.models.LoginModel
import com.dig.digibrain.models.RegisterModel

class Repository(private val apiService: ApiService) {
    // Authentication
    suspend fun login(loginModel: LoginModel) = apiService.login(loginModel)
    suspend fun register(registerModel: RegisterModel) = apiService.register(registerModel)

    // Theory
    suspend fun getDomainsForClass(number: Int, atUniversity: Boolean, languageId: Long) = apiService.getDomainsForClass(number, atUniversity, languageId)
    suspend fun getClassByNumberAndDomain(number: Int, atUniversity: Boolean, domainId: Long) = apiService.getClassByNumberAndDomain(number, atUniversity, domainId)
    suspend fun getSubjectsForClass(classId: Long) = apiService.getSubjectsForClass(classId)
    suspend fun getChaptersForSubject(subjectId: Long) = apiService.getChaptersForSubject(subjectId)
    suspend fun getLessonsForChapter(chapterId: Long) = apiService.getLessonsForChapter(chapterId)
}