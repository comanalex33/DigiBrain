package com.dig.digibrain.interfaces

import com.dig.digibrain.models.QuizTypeModel

interface IQuizTypeChanged {
    fun changeQuizType(value: QuizTypeModel)
}