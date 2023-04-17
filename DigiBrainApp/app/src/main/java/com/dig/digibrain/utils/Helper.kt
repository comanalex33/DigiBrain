package com.dig.digibrain.utils

import android.util.Base64
import android.view.View
import android.view.ViewGroup
import com.dig.digibrain.models.TokenDataModel
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

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

        fun String.countMatches(substring: String): Int {
            return ("$this.").split(substring)
                .dropLastWhile { it.isEmpty() }
                .toTypedArray().size - 1
        }

        fun String.getInitials(): String {
            val words = this.split(" ")
            return when {
                words.size == 1 -> words[0].substring(0, 1)
                words.size >= 2 -> words[0].substring(0, 1) + words[1].substring(0, 1)
                else -> ""
            }
        }

        fun convertTimestampToDateFormat(timestamp: String): String {
            val utcDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
            utcDateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = utcDateFormat.parse(timestamp)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return dateFormat.format(date)
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