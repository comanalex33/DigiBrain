package com.dig.digibrain.utils

data class Resource<out T>(val status: Status, val data: T?, val message: String?, val invalidFields: List<String>?) {
    companion object {
        fun <T> success(data: T): Resource<T> = Resource(
            status = Status.SUCCESS,
            data = data,
            message = null,
            invalidFields = null)

        fun <T> error(data: T?, message: String?, invalidFields: List<String>? = null): Resource<T> = Resource(
            status = Status.ERROR,
            data = data,
            message = message,
            invalidFields = invalidFields)

        fun <T> loading(data: T): Resource<T> = Resource(
            status = Status.LOADING,
            data = data,
            message = null,
            invalidFields = null)
    }
}
