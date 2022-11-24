package com.dig.digibrain.services

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.dig.digibrain.models.TokenDataModel
import com.dig.digibrain.utils.Helper.Companion.decodeBase64
import com.dig.digibrain.utils.Helper.Companion.deserializeTokenInfo

class SessionManager(context: Context) {

    companion object {
        private var preferences: SharedPreferences? = null
        private const val USER_TOKEN = "user_token"
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
}