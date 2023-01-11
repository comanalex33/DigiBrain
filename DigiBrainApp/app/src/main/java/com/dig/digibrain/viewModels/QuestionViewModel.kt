package com.dig.digibrain.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.dig.digibrain.models.ErrorResponseModel
import com.dig.digibrain.models.postModels.quiz.QuizReportPostModel
import com.dig.digibrain.models.quiz.QuestionAnswerModel
import com.dig.digibrain.services.server.Repository
import com.dig.digibrain.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import retrofit2.HttpException
import java.io.IOException

class QuestionViewModel(private val repository: Repository): ViewModel() {

    fun getRandomQuestions(number: Int, difficulty: String, type: String, languageId: Long) = liveData(
        Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.getRandomQuestions(number, difficulty, type, languageId)))
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

    fun getRandomQuestionsForSubject(subjectId: Long, number: Int, difficulty: String, type: String, languageId: Long) = liveData(
        Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.getRandomQuestionsForSubject(subjectId, number, difficulty, type, languageId)))
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

    fun getQuestionAnswers(questionId: Long) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.getQuestionAnswers(questionId)))
        } catch (throwable: Throwable) {
            when(throwable) {
                is IOException -> {
                    emit(Resource.error(
                        data = null,
                        message = throwable.message ?: "Network error"))
                }
                is HttpException -> {
                    try {
                        val gson = Gson()
                        val errorModel = gson.fromJson(throwable.response()?.errorBody()?.string(), ErrorResponseModel::class.java)

                        emit(Resource.error(
                            data = null,
                            message = errorModel.message,
                            invalidFields = errorModel.invalidFeilds))
                    } catch (exception: Exception) {
                        emit(Resource.error(
                            data = null,
                            message = "Error occurred!"))
                    }
                }
            }
        }
    }

    fun createQuizForUser(username: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.createQuizForUser(username)))
        } catch (throwable: Throwable) {
            when(throwable) {
                is IOException -> {
                    emit(Resource.error(
                        data = null,
                        message = throwable.message ?: "Network error"))
                }
                is HttpException -> {
                    try {
                        val gson = Gson()
                        val errorModel = gson.fromJson(throwable.response()?.errorBody()?.string(), ErrorResponseModel::class.java)

                        emit(Resource.error(
                            data = null,
                            message = errorModel.message,
                            invalidFields = errorModel.invalidFeilds))
                    } catch (exception: Exception) {
                        emit(Resource.error(
                            data = null,
                            message = "Error occurred!"))
                    }
                }
            }
        }
    }

    fun addQuestionToQuiz(model: QuestionAnswerModel) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.addQuestionToQuiz(model)))
        } catch (throwable: Throwable) {
            when(throwable) {
                is IOException -> {
                    emit(Resource.error(
                        data = null,
                        message = throwable.message ?: "Network error"))
                }
                is HttpException -> {
                    try {
                        val gson = Gson()
                        val errorModel = gson.fromJson(throwable.response()?.errorBody()?.string(), ErrorResponseModel::class.java)

                        emit(Resource.error(
                            data = null,
                            message = errorModel.message,
                            invalidFields = errorModel.invalidFeilds))
                    } catch (exception: Exception) {
                        emit(Resource.error(
                            data = null,
                            message = "Error occurred!"))
                    }
                }
            }
        }
    }

    fun addUserReport(model: QuizReportPostModel) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.addUserReport(model)))
        } catch (throwable: Throwable) {
            when(throwable) {
                is IOException -> {
                    emit(Resource.error(
                        data = null,
                        message = throwable.message ?: "Network error"))
                }
                is HttpException -> {
                    try {
                        val gson = Gson()
                        val errorModel = gson.fromJson(throwable.response()?.errorBody()?.string(), ErrorResponseModel::class.java)

                        emit(Resource.error(
                            data = null,
                            message = errorModel.message,
                            invalidFields = errorModel.invalidFeilds))
                    } catch (exception: Exception) {
                        emit(Resource.error(
                            data = null,
                            message = "Error occurred!"))
                    }
                }
            }
        }
    }
}