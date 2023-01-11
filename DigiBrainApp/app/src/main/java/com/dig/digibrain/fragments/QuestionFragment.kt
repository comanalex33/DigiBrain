package com.dig.digibrain.fragments

import androidx.fragment.app.Fragment
import com.dig.digibrain.models.postModels.quiz.QuizStatusModel
import com.dig.digibrain.models.quiz.AnswerModel

abstract class QuestionFragment: Fragment() {
    abstract fun getScore(): Double
    abstract fun answerQuestion(): Boolean
    abstract fun getSelectedAnswers(): List<AnswerModel>
}