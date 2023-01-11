package com.dig.digibrain.utils

import android.util.Base64
import android.view.View
import android.view.ViewGroup
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
        fun String.splitKeeping(str: String): List<String> {
            return this.split(str).flatMap {listOf(it, str)}.dropLast(1).filterNot {it.isEmpty()}
        }

        fun ViewGroup.forAllChildren(forOneChild: (v: View) -> Unit) {
            forOneChild(this)
            for (cx in 0 until childCount) {
                val child = getChildAt(cx)
                if (child is ViewGroup)
                    child.forAllChildren(forOneChild)
                else
                    forOneChild(child)
            }
        }
    }
}