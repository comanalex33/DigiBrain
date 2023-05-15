package com.dig.digibrain.models.learnPaths

class LearnPathExpandedModel(
    var id: Long,
    var title: String,
    var description: String,
    var author: String,
    var date: String,
    var started: Long,
    var subjectId: Long,
    var imageName: String?,
    var sections: List<LearnPathSection>)
