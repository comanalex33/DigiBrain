package com.dig.digibrain.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dig.digibrain.services.server.ApiService
import com.dig.digibrain.services.server.Repository

class ViewModelFactory(private val apiService: ApiService): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(Repository(apiService)) as T
        }
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(Repository(apiService)) as T
        }
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(Repository(apiService)) as T
        }
        if (modelClass.isAssignableFrom(LearnViewModel::class.java)) {
            return LearnViewModel(Repository(apiService)) as T
        }
        if (modelClass.isAssignableFrom(SubjectViewModel::class.java)) {
            return SubjectViewModel(Repository(apiService)) as T
        }
        if (modelClass.isAssignableFrom(AddSubjectViewModel::class.java)) {
            return  AddSubjectViewModel(Repository(apiService)) as T
        }
        if (modelClass.isAssignableFrom(QuestionViewModel::class.java)) {
            return  QuestionViewModel(Repository(apiService)) as T
        }
        if (modelClass.isAssignableFrom(AddQuestionViewModel::class.java)) {
            return  AddQuestionViewModel(Repository(apiService)) as T
        }
        if (modelClass.isAssignableFrom(ReviewLessonsViewModel::class.java)) {
            return  ReviewLessonsViewModel(Repository(apiService)) as T
        }
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            return  StatisticsViewModel(Repository(apiService)) as T
        }
        throw java.lang.IllegalArgumentException("Unknown class name")
    }
}