package com.dig.digibrain.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.dig.digibrain.models.ErrorResponseModel
import com.dig.digibrain.models.postModels.subject.ChapterPostModel
import com.dig.digibrain.models.postModels.subject.LessonPostModel
import com.dig.digibrain.services.server.Repository
import com.dig.digibrain.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import retrofit2.HttpException
import retrofit2.http.Header
import java.io.IOException

class SubjectViewModel(private val repository: Repository): ViewModel() {

    fun getChaptersForSubject(subjectId: Long) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.getChaptersForSubject(subjectId)))
        } catch (throwable: Throwable) {
            when(throwable) {
                is IOException -> {
                    emit(
                        Resource.error(
                        data = null,
                        message = throwable.message ?: "Network error"))
                }
                is HttpException -> {
                    try {
                        val gson = Gson()
                        val errorModel = gson.fromJson(throwable.response()?.errorBody()?.string(), ErrorResponseModel::class.java)

                        emit(
                            Resource.error(
                            data = null,
                            message = errorModel.message,
                            invalidFields = errorModel.invalidFeilds))
                    } catch (exception: Exception) {
                        emit(
                            Resource.error(
                            data = null,
                            message = "Error occurred!"))
                    }
                }
            }
        }
    }

    fun getLessonsForChapter(chapterId: Long) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.getLessonsForChapter(chapterId)))
        } catch (throwable: Throwable) {
            when(throwable) {
                is IOException -> {
                    emit(
                        Resource.error(
                            data = null,
                            message = throwable.message ?: "Network error"))
                }
                is HttpException -> {
                    try {
                        val gson = Gson()
                        val errorModel = gson.fromJson(throwable.response()?.errorBody()?.string(), ErrorResponseModel::class.java)

                        emit(
                            Resource.error(
                                data = null,
                                message = errorModel.message,
                                invalidFields = errorModel.invalidFeilds))
                    } catch (exception: Exception) {
                        emit(
                            Resource.error(
                                data = null,
                                message = "Error occurred!"))
                    }
                }
            }
        }
    }

    fun addChapter(authHeader: String, model: ChapterPostModel) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.addChapter(authHeader, model)))
        } catch (throwable: Throwable) {
            when(throwable) {
                is IOException -> {
                    emit(
                        Resource.error(
                            data = null,
                            message = throwable.message ?: "Network error"))
                }
                is HttpException -> {
                    try {
                        val gson = Gson()
                        val errorModel = gson.fromJson(throwable.response()?.errorBody()?.string(), ErrorResponseModel::class.java)

                        emit(
                            Resource.error(
                                data = null,
                                message = errorModel.message,
                                invalidFields = errorModel.invalidFeilds))
                    } catch (exception: Exception) {
                        emit(
                            Resource.error(
                                data = null,
                                message = "Error occurred!"))
                    }
                }
            }
        }
    }

    fun addLesson(authHeader: String, model: LessonPostModel) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.addLesson(authHeader, model)))
        } catch (throwable: Throwable) {
            when(throwable) {
                is IOException -> {
                    emit(
                        Resource.error(
                            data = null,
                            message = throwable.message ?: "Network error"))
                }
                is HttpException -> {
                    try {
                        val gson = Gson()
                        val errorModel = gson.fromJson(throwable.response()?.errorBody()?.string(), ErrorResponseModel::class.java)

                        emit(
                            Resource.error(
                                data = null,
                                message = errorModel.message,
                                invalidFields = errorModel.invalidFeilds))
                    } catch (exception: Exception) {
                        emit(
                            Resource.error(
                                data = null,
                                message = "Error occurred!"))
                    }
                }
            }
        }
    }
}