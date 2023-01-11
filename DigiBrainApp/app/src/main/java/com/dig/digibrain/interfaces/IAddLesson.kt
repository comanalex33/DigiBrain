package com.dig.digibrain.interfaces

interface IAddLesson {
    fun addLesson(chapterId: Long, title: String, text: String)
}