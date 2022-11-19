package com.dig.digibrain.services.server

import com.dig.digibrain.models.LoginModel
import com.dig.digibrain.models.RegisterModel

class Repository(private val apiService: ApiService) {
    suspend fun login(loginModel: LoginModel) = apiService.login(loginModel)
    suspend fun register(registerModel: RegisterModel) = apiService.register(registerModel)
}