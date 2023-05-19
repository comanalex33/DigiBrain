package com.dig.digibrain.models.learnPaths

class LearnPathQuizStatusModel(
    var id: Long,
    var username: String,
    var pathLearnId: Long,
    var sectionNumber: Long,
    var lessonNumber: Long,
    var score: Long
)