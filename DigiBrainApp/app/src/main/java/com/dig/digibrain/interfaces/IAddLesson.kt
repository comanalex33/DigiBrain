package com.dig.digibrain.interfaces

import android.view.View

interface IAddLesson {
    fun addLesson(chapterId: Long, title: String, text: String)
}