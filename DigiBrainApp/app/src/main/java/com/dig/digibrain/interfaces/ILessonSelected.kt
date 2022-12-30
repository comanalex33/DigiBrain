package com.dig.digibrain.interfaces

import com.dig.digibrain.models.subject.LessonModel

interface ILessonSelected {
    fun openLesson(model: LessonModel)
}