package com.dig.digibrain.models.learnPaths

class LearnPathSection(
    var id: Long,
    var number: Int,
    var title: String,
    var iconId: Long,
    var pathLearnId: Long,
    var lessons: List<LearnPathLesson>
)
