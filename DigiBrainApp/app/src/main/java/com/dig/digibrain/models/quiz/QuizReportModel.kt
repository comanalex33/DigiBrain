package com.dig.digibrain.models.quiz

class QuizReportModel(
    var id: Long,
    var username: String,
    var quizType: String,
    var score: Double,
    var numberOfQuestions: Int,
    var subjectId: Long,
    var difficulty: String)