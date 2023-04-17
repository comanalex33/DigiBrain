package com.dig.digibrain.models.learnPaths

class LearnPathLesson(
    var id: Long,
    var number: Int,
    var title: String,
    var description: String,
    var pathSectionId: Long,
    var quiz: List<LearnPathQuiz>,
    var theory: List<LearnPathTheory>
)
