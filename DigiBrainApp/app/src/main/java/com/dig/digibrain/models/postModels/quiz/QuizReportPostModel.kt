package com.dig.digibrain.models.postModels.quiz

class QuizReportPostModel(
    var username: String,
    var quizType: String,
    var score: Double,
    var numberOfQuestions: Int,
    var subjectId: Long,
    var difficulty: String)