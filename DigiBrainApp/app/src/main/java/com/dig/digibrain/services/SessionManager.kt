package com.dig.digibrain.services

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.dig.digibrain.models.ObjectStorageInfoModel
import com.dig.digibrain.models.TokenDataModel
import com.dig.digibrain.utils.Helper.Companion.decodeBase64
import com.dig.digibrain.utils.Helper.Companion.deserializeTokenInfo

class SessionManager(context: Context) {

    companion object {
        private var preferences: SharedPreferences? = null
        private const val USER_TOKEN = "user_token"
        private const val STORAGE_ACCESS_KEY = "storage_access_key"
        private const val STORAGE_SECRET_KEY = "storage_secret_key"
        private const val BUCKET_NAME = "bucket_name"
        private const val BUCKET_REGION = "bucket_region"
    }

    init {
        if(preferences == null) {
            val masterKeyAlias = MasterKey.Builder (context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            preferences = EncryptedSharedPreferences.create(
                context,
                "encrypted_preferences",
                masterKeyAlias,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }

    fun setAuthToken(token: String): Boolean {
        preferences?.apply {
            val editor = this.edit()
            editor.putString(USER_TOKEN, token)
            editor.apply()
            return true
        }
        return false
    }

    fun disconnect(): Boolean {
        preferences?.apply {
            val editor = this.edit()
            editor.remove(USER_TOKEN)
            editor.apply()
            return true
        }
        return false
    }

    fun getBearerAuthToken(): String? {
        preferences?.apply {
            return "Bearer ${this.getString(USER_TOKEN, null)}"
        }
        return null
    }

    fun getUserName(): String? {
        preferences?.apply {
            val token = this.getString(USER_TOKEN, null)
            val tokenInfo = getTokenInfo(token)
            tokenInfo?.apply {
                return this.name
            }
        }
        return null
    }

    fun getUserRole(): String? {
        preferences?.apply {
            val token = this.getString(USER_TOKEN, null)
            val tokenInfo = getTokenInfo(token)
            tokenInfo?.apply {
                return this.roles
            }
        }
        return null
    }

    private fun getTokenInfo(token: String?): TokenDataModel? {
        token?.apply {
            this.split(".").apply {
                val data = this[1].decodeBase64()
                return data.deserializeTokenInfo()
            }
        }
        return null
    }

    fun setObjectStorageInfo(model: ObjectStorageInfoModel) {
        preferences?.apply {
            val editor = this.edit()
            editor.putString(STORAGE_ACCESS_KEY, model.readAccessKey)
            editor.putString(STORAGE_SECRET_KEY, model.readSecretKey)
            editor.putString(BUCKET_NAME, model.bucketName)
            editor.putString(BUCKET_REGION, model.bucketRegion)
            editor.apply()
        }
    }

    fun getObjectStorageInfo(): ObjectStorageInfoModel? {
        preferences?.apply {
            val accessKey = this.getString(STORAGE_ACCESS_KEY, null)
            val secretKey = this.getString(STORAGE_SECRET_KEY, null)
            val bucketName = this.getString(BUCKET_NAME, null)
            val bucketRegion = this.getString(BUCKET_REGION, null)
            if(accessKey != null && secretKey != null && bucketName != null && bucketRegion != null) {
                return ObjectStorageInfoModel(
                    readAccessKey = accessKey,
                    readSecretKey = secretKey,
                    bucketName = bucketName,
                    bucketRegion = bucketRegion
                )
            }
        }
        return null
    }
}