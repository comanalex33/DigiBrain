package com.dig.digibrain.fragments

import androidx.fragment.app.Fragment
import com.dig.digibrain.models.quiz.AnswerModel

abstract class QuestionFragment: Fragment() {
    abstract fun answerQuestion(): Boolean
    abstract fun getSelectedAnswers(): List<AnswerModel>
}