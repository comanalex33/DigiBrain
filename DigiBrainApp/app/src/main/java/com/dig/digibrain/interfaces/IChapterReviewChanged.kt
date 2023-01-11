package com.dig.digibrain.interfaces

import com.dig.digibrain.models.subject.ChapterModel

interface IChapterReviewChanged {
    fun changeChapter(value: ChapterModel)
}