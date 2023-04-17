package com.dig.digibrain.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.dig.digibrain.models.ErrorResponseModel
import com.dig.digibrain.services.server.Repository
import com.dig.digibrain.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import retrofit2.HttpException
import java.io.IOException

class LearnPathViewModel(private val repository: Repository): ViewModel() {

    fun getLearnPaths() = liveData(
        Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.getLearnPaths()))
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

    fun getSubjectsForIds(subjectIds: List<Long>) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.getSubjectsForIds(subjectIds)))
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

    fun getClassById(id: Long) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = repository.getClassById(id)))
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