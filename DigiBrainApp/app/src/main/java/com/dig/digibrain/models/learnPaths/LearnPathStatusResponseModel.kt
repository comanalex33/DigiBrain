package com.dig.digibrain.models.learnPaths

class LearnPathStatusResponseModel(
    var id: Long,
    var username: String,
    var pathLearnId: Long,
    var sectionNumber: Long,
    var lessonNumber: Long,
    var theoryNumber: Long,
    var finished: Boolean,
    var quiz: List<LearnPathQuizStatusModel>
)