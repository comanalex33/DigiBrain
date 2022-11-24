package com.dig.digibrain.utils

import android.util.Base64
import com.dig.digibrain.models.TokenDataModel
import com.google.gson.Gson

class Helper {
    companion object {
        fun String.deserializeTokenInfo(): TokenDataModel {
            val gson = Gson()
            return gson.fromJson(this, TokenDataModel::class.java)
        }
        fun String.decodeBase64(): String {
            return Base64.decode(this, Base64.DEFAULT).decodeToString()
        }
    }
}